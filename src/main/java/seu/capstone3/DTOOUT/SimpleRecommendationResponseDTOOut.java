package seu.capstone3.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class SimpleRecommendationResponseDTOOut {

    private String message;
    private List<PlayerPickDTOOut> suggested;
    private List<PlayerPickDTOOut> alternatives;
    private int applicants;
}
