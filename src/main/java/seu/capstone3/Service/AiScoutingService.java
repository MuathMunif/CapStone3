package seu.capstone3.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;

import seu.capstone3.Api.ApiException;
import seu.capstone3.DTOOUT.PlayerPickDTOOut;
import seu.capstone3.DTOOUT.PlayerSWAnalysisDTOOut;
import seu.capstone3.DTOOUT.SimpleRecommendationResponseDTOOut;
import seu.capstone3.DTOOUT.TrainingPlanSimpleDTOOut;
import seu.capstone3.Model.Player;
import seu.capstone3.Model.RecruitmentOpportunity;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiScoutingService {

    private final ChatClient chatClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${scouting.minScore:60}")
    private double minScore;

    @Value("${scouting.strongThreshold:75}")
    private double strongThreshold;

    @Value("${scouting.maxSuggested:3}")
    private int maxSuggested;

    @Value("${scouting.maxAlternatives:5}")
    private int maxAlternatives;

    @Autowired
    public AiScoutingService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /** Always return players if there are applicants. Keep it simple but informative. */
    public SimpleRecommendationResponseDTOOut recommend(RecruitmentOpportunity opp, List<Player> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return new SimpleRecommendationResponseDTOOut(
                    "No applicants yet.",
                    List.of(),
                    List.of(),
                    0
            );
        }

        String prompt = buildPrompt(opp, candidates, minScore, strongThreshold);

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .temperature(0.1)
                .build();

        String content = chatClient
                .prompt()
                .system("""
                        You are a precise talent-scout assistant.
                        Respond in ENGLISH only.
                        Return ONLY a single JSON object; no markdown, no code fences.
                        """)
                .user(prompt)
                .options(options)
                .call()
                .content();

        if (content == null || content.isBlank()) {
            return fallbackListing("AI returned empty content. Showing applicants as tentative.", candidates);
        }

        String jsonOnly = sanitizeToJson(content);

        // Expected JSON:
        // { "ranking": [ {playerId, playerName, score, reason}, ... ] }
        List<PlayerPickDTOOut> ranking = parseRanking(jsonOnly);
        if (ranking.isEmpty()) {
            return fallbackListing("No clear ranking from AI. Showing applicants as tentative.", candidates);
        }

        // Bucket into suggested vs alternatives (but DO NOT expose noneStrong flag)
        List<PlayerPickDTOOut> suggested = ranking.stream()
                .filter(p -> p.getScore() != null && p.getScore() >= minScore)
                .sorted(Comparator.comparing(PlayerPickDTOOut::getScore).reversed())
                .limit(maxSuggested)
                .collect(Collectors.toList());

        boolean noneStrong = ranking.stream()
                .noneMatch(p -> p.getScore() != null && p.getScore() >= strongThreshold);

        Set<Integer> usedIds = suggested.stream()
                .filter(p -> p.getPlayerId() != null)
                .map(PlayerPickDTOOut::getPlayerId)
                .collect(Collectors.toSet());

        List<PlayerPickDTOOut> alternatives = ranking.stream()
                .filter(p -> p.getPlayerId() != null && !usedIds.contains(p.getPlayerId()))
                .limit(maxAlternatives)
                .collect(Collectors.toList());

        if (suggested.isEmpty() && !ranking.isEmpty()) {
            suggested = List.of(ranking.get(0));
            usedIds.add(ranking.get(0).getPlayerId());
            alternatives = ranking.stream()
                    .skip(1)
                    .limit(maxAlternatives)
                    .collect(Collectors.toList());
            noneStrong = true;
        }

        String message;
        if (!noneStrong) {
            message = "Suggested picks based on fit.";
        } else if (!suggested.isEmpty()) {
            message = "No clear strong fit; showing tentative picks.";
        } else {
            message = "No suitable candidate; showing tentative picks.";
        }

        return new SimpleRecommendationResponseDTOOut(
                message,
                suggested,
                alternatives,
                candidates.size()
        );
    }

    private String buildPrompt(RecruitmentOpportunity opp, List<Player> candidates, double minScore, double strongThreshold) {
        String oppCategory = extractCategoryName(opp);

        String playersBlock = candidates.stream().map(p ->
                """
                {
                  "id": %d,
                  "name": "%s",
                  "age": %d,
                  "location": "%s",
                  "height": %.2f,
                  "weight": %.2f,
                  "description": "%s",
                  "skills": "%s",
                  "category": "%s"
                }
                """.formatted(
                        p.getId(),
                        safe(p.getName()),
                        nz(p.getAge()),
                        safe(p.getLocation()),
                        nd(p.getHeight()),
                        nd(p.getWeight()),
                        safe(p.getDescription()),
                        safe(p.getSkills()),
                        safe(extractCategoryName(p))
                )
        ).collect(Collectors.joining(",\n"));

        String oppBlock = """
            {
              "id": %d,
              "club": "%s",
              "description": "%s",
              "category": "%s"
            }
            """.formatted(
                opp.getId(),
                opp.getClub()!=null ? safe(opp.getClub().getName()) : "N/A",
                safe(opp.getDescription()),
                oppCategory
        );

        return """
        Rank candidates for a recruitment opportunity (football context).

        Opportunity:
        %s

        Candidates:
        [
        %s
        ]

        Rules:
        - Prioritize the same category as the opportunity category ("%s").
        - Category mismatch can still appear but should usually score <= %.1f.
        - Score each candidate from 0 to 100 based on overall fit (skills/role/description/physical/age/location).
        - Give short, plain-English reasons.
        - Return ALL candidates in a single array "ranking", sorted by score desc.

        Return EXACT JSON (no extra fields, no markdown):
        {
          "ranking": [
            {"playerId": <number>, "playerName": "<string>", "score": <0-100>, "reason": "<short reason>"}
          ]
        }
        """.formatted(oppBlock, playersBlock, oppCategory, minScore);
    }

    private List<PlayerPickDTOOut> parseRanking(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            if (!root.has("ranking") || !root.get("ranking").isArray()) return List.of();
            List<PlayerPickDTOOut> picks = mapper.convertValue(root.get("ranking"),
                    new TypeReference<List<PlayerPickDTOOut>>() {});
            return picks.stream().map(p -> {
                if (p.getReason() != null && p.getReason().length() > 160) {
                    p.setReason(p.getReason().substring(0, 160).trim());
                }
                return p;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("AI parse error: {}", e.getMessage());
            return List.of();
        }
    }

    private SimpleRecommendationResponseDTOOut fallbackListing(String msg, List<Player> candidates) {
        List<PlayerPickDTOOut> all = candidates.stream()
                .map(p -> new PlayerPickDTOOut(p.getId(), p.getName(), null, "Provisional."))
                .collect(Collectors.toList());

        List<PlayerPickDTOOut> suggested = all.isEmpty() ? List.of() : List.of(all.get(0));
        List<PlayerPickDTOOut> alternatives = all.stream().skip(1).limit(maxAlternatives).collect(Collectors.toList());

        return new SimpleRecommendationResponseDTOOut(
                msg,
                suggested,
                alternatives,
                candidates.size()
        );
    }

    /** Strip code fences and isolate the largest JSON object. */
    private String sanitizeToJson(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.startsWith("```")) {
            int firstNl = t.indexOf('\n');
            if (firstNl >= 0) t = t.substring(firstNl + 1);
            if (t.endsWith("```")) t = t.substring(0, t.length() - 3);
        }
        t = t.trim();
        int start = t.indexOf('{');
        int end = t.lastIndexOf('}');
        if (start >= 0 && end > start) t = t.substring(start, end + 1);
        return t.trim();
    }

    private String extractCategoryName(Object obj) {
        if (obj == null) return "";
        try {
            var mId = obj.getClass().getMethod("getCategory");
            Object cat = mId.invoke(obj);
            if (cat != null) {
                var mName = cat.getClass().getMethod("getName");
                Object v = mName.invoke(cat);
                return v != null ? v.toString() : "";
            }
        } catch (Exception ignored) {}
        try {
            var mName = obj.getClass().getMethod("getCategoryName");
            Object v = mName.invoke(obj);
            return v != null ? v.toString() : "";
        } catch (Exception ignored) {}
        try {
            var mId = obj.getClass().getMethod("getCategoryId");
            Object v = mId.invoke(obj);
            return v != null ? v.toString() : "";
        } catch (Exception ignored) {}
        return "";
    }

    private String safe(String s){ return s==null? "": s.replace("\"","'"); }
    private int nz(Integer i){ return i==null? 0: i; }
    private double nd(Double d){ return d==null? 0.0: d; }


    public PlayerSWAnalysisDTOOut analyzePlayerStrengthsWeaknesses(Player player) {
        try {
            String prompt = """
                You are a professional football scout and analyst. 
                Analyze the following player and return ONLY valid JSON with these fields:
                - strengths: list of short sentences for key strengths
                - weaknesses: list of short sentences for key weaknesses
                - skillScores: object with main skills (speed, stamina, passing, shooting, dribbling, positioning, vision) each scored 0-100
                - summary: short summary (max 2 lines)
                - confidence: value between 0 and 1 for your confidence

                Player data:
                Name: %s
                Age: %s
                Height: %s
                Weight: %s
                Position: %s
                Skills: %s
                Description: %s

                Important rules:
                - Output must be valid JSON only, no extra text.
                - If some data is missing, make reasonable assumptions but do not invent unrealistic facts.
                """.formatted(
                    safe(player.getName()),
                    safe(player.getAge()),
                    safe(player.getHeight()),
                    safe(player.getWeight()),
                    safe(player.getSkills()),
                    safe(player.getDescription())
            );

            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .temperature(0.2)
                    .model("gpt-4o") // change if needed
                    .build();

            String raw = chatClient.prompt(prompt)
                    .options(options)
                    .call()
                    .content();

            JsonNode json = mapper.readTree(raw);

            PlayerSWAnalysisDTOOut dto = new PlayerSWAnalysisDTOOut();
            dto.setPlayerId(player.getId());
            dto.setPlayerName(player.getName());
            dto.setSummary(json.path("summary").asText(null));
            dto.setConfidence(json.path("confidence").asDouble(0.6));

            List<String> strengths = new ArrayList<>();
            json.path("strengths").forEach(n -> strengths.add(n.asText()));
            dto.setStrengths(strengths);

            List<String> weaknesses = new ArrayList<>();
            json.path("weaknesses").forEach(n -> weaknesses.add(n.asText()));
            dto.setWeaknesses(weaknesses);

            Map<String, Integer> scores = new LinkedHashMap<>();
            JsonNode scoresNode = json.path("skillScores");
            scoresNode.fieldNames().forEachRemaining(k -> scores.put(k, scoresNode.path(k).asInt()));
            dto.setSkillScores(scores);

            return dto;

        } catch (Exception e) {
            log.error("AI analysis failed", e);
            return new PlayerSWAnalysisDTOOut(
                    player.getId(),
                    player.getName(),
                    List.of("Shows tactical discipline"),
                    List.of("Insufficient data for full evaluation"),
                    Map.of("speed", 60, "stamina", 55, "passing", 58, "shooting", 57, "dribbling", 59, "positioning", 60, "vision", 56),
                    "Preliminary analysis based on limited data.",
                    0.4
            );
        }
    }


    // AiScoutingService.java
    public TrainingPlanSimpleDTOOut generateAutoTrainingPlan(Player player, int days) {
        String sport = extractSport(player); // يقرأ category فقط

        String prompt = """
        You are a sports coach. Based ONLY on the player's sport (category) and description,
        generate a SIMPLE plan and decide the primary focus yourself.
        Return ONLY a single valid JSON object (no markdown) with:
        {
          "playerId": <number>,
          "playerName": "<string>",
          "sport": "<string>",
          "days": <number>,
          "focus": "<speed|stamina|strength|agility|balanced>",
          "plan": [
            { "day": <1..days>, "title": "<short>", "workout": "<one line>", "notes": "<short>" }
          ]
        }

        Rules:
        - Use sport-specific terminology for the given sport.
        - Include at least 1 rest day per week.
        - Vary titles; avoid generic placeholders (e.g., "General session", "Focus on form").
        - Days MUST equal %d (1..%d). Output JSON only.

        Player:
        Name: %s
        Sport (category): %s
        Description: %s
        Requested days: %d
        """.formatted(days, days,
                safe(player.getName()), sport, safe(player.getDescription()), days);

        try {
            OpenAiChatOptions opts = OpenAiChatOptions.builder()
                    .model("gpt-4o")
                    .temperature(0.25)
                    .build();

            String raw = chatClient.prompt(prompt).options(opts).call().content();
            JsonNode json = parseJsonLenient(raw); // ينظف أي code fences ويقرأ JSON

            TrainingPlanSimpleDTOOut dto = mapPlanJson(player, json, sport, days);
            if (dto.getPlan() == null || dto.getPlan().isEmpty()) {
                throw new IllegalStateException("Empty AI plan");
            }
            ensureLength(dto.getPlan(), dto.getDays()); // ضبط 1..days
            return dto;

        } catch (Exception e) {

            throw new ApiException("AI_TRAINING_PLAN_FAILED: " + e.getMessage());
        }
    }

    private JsonNode parseJsonLenient(String raw) throws Exception {
        String s = raw == null ? "" : raw.trim();
        int start = s.indexOf('{');
        int end = s.lastIndexOf('}');
        if (start >= 0 && end > start) s = s.substring(start, end + 1);
        return mapper.readTree(s);
    }

    private TrainingPlanSimpleDTOOut mapPlanJson(Player player, JsonNode json, String sport, int days) {
        TrainingPlanSimpleDTOOut dto = new TrainingPlanSimpleDTOOut();
        dto.setPlayerId(player.getId());
        dto.setPlayerName(player.getName());
        dto.setSport(json.path("sport").asText(sport));
        dto.setDays(json.path("days").asInt(days));
        dto.setFocus(json.path("focus").asText("balanced"));
        List<TrainingPlanSimpleDTOOut.Day> out = new ArrayList<>();
        JsonNode arr = json.path("plan");
        if (arr != null && arr.isArray()) {
            for (JsonNode n : arr) {
                TrainingPlanSimpleDTOOut.Day d = new TrainingPlanSimpleDTOOut.Day();
                d.setDay(n.path("day").asInt(out.size() + 1));
                d.setTitle(n.path("title").asText("Session"));
                d.setWorkout(n.path("workout").asText("Sport-specific drills 30–40 min"));
                d.setNotes(n.path("notes").asText("Quality over volume"));
                out.add(d);
            }
        }
        dto.setPlan(out);
        return dto;
    }

    private void ensureLength(List<TrainingPlanSimpleDTOOut.Day> plan, int days) {
        if (plan == null) return;
        while (plan.size() > days) plan.remove(plan.size() - 1);
        while (plan.size() < days) plan.add(new TrainingPlanSimpleDTOOut.Day(plan.size() + 1, "Rest", "Walk 20 min + stretch", "Hydrate"));
        for (int i = 0; i < plan.size(); i++) plan.get(i).setDay(i + 1);
    }

    private String extractSport(Player p) {
        try {
            if (p.getCategory() != null && p.getCategory().getName() != null)
                return p.getCategory().getName();
        } catch (Exception ignored) {}
        return "Unknown";
    }
    private String safe(Object v) { return v == null ? "N/A" : String.valueOf(v); }

}
