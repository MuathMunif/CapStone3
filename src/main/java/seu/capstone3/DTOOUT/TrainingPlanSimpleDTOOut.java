package seu.capstone3.DTOOUT;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingPlanSimpleDTOOut {
    private Integer playerId;
    private String playerName;
    private String sport;
    @Min(7) @Max(60)
    private Integer days;
    private String focus;
    private List<Day> plan;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Day {
        private Integer day;
        private String title;
        private String workout;
        private String notes;
    }
}
