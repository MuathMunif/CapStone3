package seu.capstone3.DTOIN;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestJoiningDTOIn {

    @NotNull(message = "Player ID must not be null")
    private Integer player_id;

    @NotNull(message = "Recruitment Opportunity ID must not be null")
    private Integer recruitment_opportunity_id;
}
