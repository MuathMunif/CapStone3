package seu.capstone3.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestJoiningDTOOut {

    private Integer requestId;
    private String playerName;
    private String status;

}
