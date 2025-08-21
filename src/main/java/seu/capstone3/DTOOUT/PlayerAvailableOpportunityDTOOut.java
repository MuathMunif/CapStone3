package seu.capstone3.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerAvailableOpportunityDTOOut {

    private String clubName;
    private String title;
    private String description;
    private String status;
}
