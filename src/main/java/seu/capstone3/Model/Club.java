package seu.capstone3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "The CR must be not empty")
    @Column(columnDefinition = "varchar(10) not null unique")
    private String cr;

    @NotEmpty(message = "The name must be not empty")
    @Column(columnDefinition = "varchar(50) not null")
    private String name;

    @Email(message = "The email must be valid email")
    @Column(columnDefinition = "varchar(50) not null unique")
    private String email;

    @NotEmpty(message = "The phoneNumber must be not empty")
    @Column(columnDefinition = "varchar(10) not null unique")
    private String phoneNumber;

    @NotEmpty(message = "The location must be not empty")
    @Column(columnDefinition = "varchar(50) not null")
    private String location;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "club")
    @JsonIgnore
    private Set<RecruitmentOpportunity> recruitmentOpportunities;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "club")
    @JsonIgnore
    private Set<Player> players;

    @ManyToOne
    private Category category;

}
