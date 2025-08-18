package seu.capstone3.DTOIN;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentDTO {

    @NotEmpty(message = "The name must be not empty")
    private String name;

    @NotEmpty(message = "The description must be not empty")
    private String description;

    @NotNull(message = "The numberOfPlayers must be not empty")
    @Positive(message = "The numberOfPlayers must be a valid number")
    private Integer numberOfPlayers;

    @FutureOrPresent(message = "The start date must be in present or future")
    private LocalDate startDate;

    @Future(message = "The start date must be in future")
    private LocalDate endDate;

    @NotEmpty(message = "The location must be not empty")
    private String location;

    @NotNull(message = "The category_id must be not null")
    private Integer category_id;


    @NotNull(message = "The sponsor_id must be not null")
    private Integer sponsor_id;


}
