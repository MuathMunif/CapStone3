package seu.capstone3.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruitmentOpportunityDTOOut {

    private String clubName;
    private String title;
    private String description;
    private String status;

    private List<RequestJoiningDTOOut> requests;
}
