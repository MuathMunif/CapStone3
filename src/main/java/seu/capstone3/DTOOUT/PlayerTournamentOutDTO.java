package seu.capstone3.DTOOUT;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PlayerTournamentOutDTO {

    private String name;
    private String startDate;
    private String endDate;
    private String sponsorName;

    public PlayerTournamentOutDTO(String name, String startData, String endDate, String sponsorName){
        this.name = name;
        this.startDate = startData;
        this.endDate = endDate;
        this.sponsorName = sponsorName;
    }
}
