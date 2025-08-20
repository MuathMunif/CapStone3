package seu.capstone3.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerSWAnalysisDTO {
    private Integer playerId;
    private String playerName;
    private List<String> strengths;        // أمثلة: "سرعة عالية", "تمركز ممتاز"
    private List<String> weaknesses;       // أمثلة: "لياقة ضعيفة", "قرارات تحت الضغط"
    private Map<String, Integer> skillScores; // { "speed": 88, "stamina": 62, ... }
    private String summary;                // ملخص نصي موجز
    private Double confidence;             // 0.0 - 1.0
}
