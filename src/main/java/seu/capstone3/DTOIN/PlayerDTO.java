package seu.capstone3.DTOIN;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDTO {

    @NotEmpty(message = "The name must be not empty")
    private String name;

    @Email(message = "The email must be a valid email")
    private String email;

    @NotEmpty(message = "The phoneNumber must be not empty")
    private String phoneNumber;

    @NotNull(message = "The age must be not empty")
    @Positive(message = "The age must be a valid number")
    private Integer age;

    @NotEmpty(message = "The location must be not empty")
    private String location;

    @NotNull(message = "The height must be not empty")
    @Positive(message = "The height must be a valid number")
    private Double height;

    @NotNull(message = "The weight must be not empty")
    @Positive(message = "The weight must be a valid number")
    private Double weight;

    @NotEmpty(message = "The description must be not empty")
    private String description;

    @NotEmpty(message = "The skills must be not empty")
    private String skills;

    @NotNull(message = "The category_id must be not null")
    private Integer category_id;
}
