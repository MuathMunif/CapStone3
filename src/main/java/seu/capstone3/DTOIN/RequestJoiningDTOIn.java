package seu.capstone3.DTOIN;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestJoiningDTOIn {

    private Integer player_id;

    private Integer recruitment_opportunity_id;
}
