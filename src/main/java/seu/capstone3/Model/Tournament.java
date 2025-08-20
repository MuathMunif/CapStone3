package seu.capstone3.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "The name must be not empty")
    @Column(columnDefinition = "varchar(50) not null")
    private String name;

    @NotEmpty(message = "The description must be not empty")
    @Column(columnDefinition = "varchar(500) not null")
    private String description;

    @NotNull(message = "The numberOfPlayers must be not empty")
    @Positive(message = "The numberOfPlayers must be a valid number")
    @Column(columnDefinition = "int not null")
    private Integer numberOfPlayers;


    @FutureOrPresent(message = "The start date must be in present or future")
    @Column(columnDefinition = "date not null")
    private LocalDate startDate;

    @Future(message = "The start date must be in future")
    @Column(columnDefinition = "date not null")
    private LocalDate endDate;

    @NotEmpty(message = "The location must be not empty")
    @Column(columnDefinition = "varchar(20) not null")
    private String location;

    @ManyToOne
    private Sponsor sponsor;

    @ManyToOne
    private Category category;

    @NotNull(message = "The number of teams must not be empty")
    @Positive(message = "The number of teams must be a valid number")
    private Integer numberOfTeams;

    @ManyToMany //todo check if the player can joining direct to tournament
    private Set<Player> players;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tournament")
    private Set<Team> teams;

    @NotEmpty(message = "Status must not be empty")
    @Column(columnDefinition = "varchar(20) not null")
    private String status = "OPEN";

    private Integer playerCounter = 0;

}
