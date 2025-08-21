package seu.capstone3.DTOIN;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentRescheduleDTOIn {

    @FutureOrPresent(message = "The start date must be in present or future")
    private LocalDate startDate;

    @Future(message = "The start date must be in future")
    private LocalDate endDate;
}
