package seu.capstone3.DTOIN;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubDTOIn {

    @NotEmpty(message = "The CR must be not empty")
    private String cr;

    @NotEmpty(message = "The name must be not empty")
    @Size(min = 4, max = 120, message = "Name must be between 4 and 120 characters")
    private String name;

    @Email(message = "The email must be valid email")
    private String email;

    @NotEmpty(message = "The phoneNumber must be not empty")
    private String phoneNumber;

    @NotEmpty(message = "The location must be not empty")
    private String location;

    @NotNull(message = "The category_id must be not null")
    private Integer category_id;
}
