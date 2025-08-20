package seu.capstone3.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerOUTDTO {

    private String name;
    private String email;
    private String phoneNumber;
    private Integer age;
    private String location;
    private Double height;
    private Double weight;
    private String description;
    private String skills;
    private String cvUrl;

    private String clubName;
    private String categoryName;
}
