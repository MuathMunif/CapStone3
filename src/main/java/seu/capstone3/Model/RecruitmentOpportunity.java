package seu.capstone3.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RecruitmentOpportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "The title must be not empty")
    @Column(columnDefinition = "varchar(20) not null")
    private String title;

    @NotEmpty(message = "The description must be not empty")
    @Column(columnDefinition = "varchar(500) not null")
    private String description;

    @Pattern(regexp = "^(OPEN|CLOSE)$", message = "The status must be either OPEN or CLOSE")
    private String status;

    @ManyToOne
    private Club club;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "recruitmentOpportunity")
    private Set<RequestJoining> requestJoinings;

    //todo check if complete or not
}
