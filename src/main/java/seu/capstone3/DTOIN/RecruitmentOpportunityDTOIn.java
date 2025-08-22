package seu.capstone3.DTOIN;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruitmentOpportunityDTOIn {

    @NotEmpty(message = "Title must not be empty")
    private String title;

    @NotEmpty(message = "Description must not be empty")
    private String description;

    @NotNull(message = "Club ID must not be null")
    private Integer club_id;

}
