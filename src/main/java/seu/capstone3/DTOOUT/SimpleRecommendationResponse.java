package seu.capstone3.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class SimpleRecommendationResponse {

    private String message;
    private List<PlayerPick> suggested;
    private List<PlayerPick> alternatives;
    private int applicants;
}
