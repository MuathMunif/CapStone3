package seu.capstone3.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor
public class SimpleRecommendationResponseDTO {

    private String message;
    private List<PlayerPickDTO> suggested;
    private List<PlayerPickDTO> alternatives;
    private int applicants;
}
