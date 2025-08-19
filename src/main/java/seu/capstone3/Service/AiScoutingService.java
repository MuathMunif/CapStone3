// src/main/java/seu/capstone3/Service/AiScoutingService.java
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

import seu.capstone3.DTOOUT.PlayerPickDTO;
import seu.capstone3.DTOOUT.SimpleRecommendationResponseDTO;
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
    public SimpleRecommendationResponseDTO recommend(RecruitmentOpportunity opp, List<Player> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return new SimpleRecommendationResponseDTO(
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
        List<PlayerPickDTO> ranking = parseRanking(jsonOnly);
        if (ranking.isEmpty()) {
            return fallbackListing("No clear ranking from AI. Showing applicants as tentative.", candidates);
        }

        // Bucket into suggested vs alternatives (but DO NOT expose noneStrong flag)
        List<PlayerPickDTO> suggested = ranking.stream()
                .filter(p -> p.getScore() != null && p.getScore() >= minScore)
                .sorted(Comparator.comparing(PlayerPickDTO::getScore).reversed())
                .limit(maxSuggested)
                .collect(Collectors.toList());

        boolean noneStrong = ranking.stream()
                .noneMatch(p -> p.getScore() != null && p.getScore() >= strongThreshold);

        Set<Integer> usedIds = suggested.stream()
                .filter(p -> p.getPlayerId() != null)
                .map(PlayerPickDTO::getPlayerId)
                .collect(Collectors.toSet());

        List<PlayerPickDTO> alternatives = ranking.stream()
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

        return new SimpleRecommendationResponseDTO(
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

    private List<PlayerPickDTO> parseRanking(String json) {
        try {
            JsonNode root = mapper.readTree(json);
            if (!root.has("ranking") || !root.get("ranking").isArray()) return List.of();
            List<PlayerPickDTO> picks = mapper.convertValue(root.get("ranking"),
                    new TypeReference<List<PlayerPickDTO>>() {});
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

    private SimpleRecommendationResponseDTO fallbackListing(String msg, List<Player> candidates) {
        List<PlayerPickDTO> all = candidates.stream()
                .map(p -> new PlayerPickDTO(p.getId(), p.getName(), null, "Provisional."))
                .collect(Collectors.toList());

        List<PlayerPickDTO> suggested = all.isEmpty() ? List.of() : List.of(all.get(0));
        List<PlayerPickDTO> alternatives = all.stream().skip(1).limit(maxAlternatives).collect(Collectors.toList());

        return new SimpleRecommendationResponseDTO(
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
}
