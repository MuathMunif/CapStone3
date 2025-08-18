package seu.capstone3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "The name must be not empty")
    @Column(columnDefinition = "varchar(20) not null")
    private String name;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "category")
    @JsonIgnore
    private Set<Player> players;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "category")
    @JsonIgnore
    private Set<Tournament> tournaments;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "category")
    @JsonIgnore
    private Set<Club> clubs;

}
