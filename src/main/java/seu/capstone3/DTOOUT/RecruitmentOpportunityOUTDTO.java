package seu.capstone3.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import seu.capstone3.DTOIN.RequestJoiningDTO;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruitmentOpportunityOUTDTO {

    private String clubName;
    private String description;
    private String status;

    private List<RequestJoiningOUTDTO> requests;
}
