package seu.capstone3.DTOOUT;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClubOUTDTO {

    private String name;
    private String email;
    private String phoneNumber;
    private String location;

    private String categoryName;
    private List<String> playerNames;
}
