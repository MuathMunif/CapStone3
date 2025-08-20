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
    private List<String> strengths;
    private List<String> weaknesses;
    private Map<String, Integer> skillScores;
    private String summary;
    private Double confidence;
}
