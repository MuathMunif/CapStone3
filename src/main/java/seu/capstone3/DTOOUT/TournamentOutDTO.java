package seu.capstone3.DTOOUT;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class TournamentOutDTO {

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private String categoryName;

    public TournamentOutDTO(String name, LocalDate startDate, LocalDate endDate,
                            String location, String categoryName){
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.categoryName = categoryName;
    }
}
