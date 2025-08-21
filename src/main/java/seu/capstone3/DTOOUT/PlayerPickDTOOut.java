package seu.capstone3.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PlayerPickDTOOut {
    private Integer playerId;
    private String playerName;
    private Double score;   // 0..100 (nullable if fallback)
    private String reason;  // short, plain English
}
