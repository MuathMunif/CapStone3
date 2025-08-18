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
public class Sponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "The name must be not empty")
    @Column(columnDefinition = "varchar(20) not null")
    private String name;

    @Email(message = "The email must be a valid email")
    @Column(columnDefinition = "varchar(30) not null")
    private String email;

    @NotEmpty(message = "The phoneNumber must be not empty")
    @Column(columnDefinition = "varchar(10) not null")
    private String phoneNumber;

    @NotEmpty(message = "The description must be not empty")
    @Column(columnDefinition = "varchar(500) not null")
    private String description;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "sponsor")
    @JsonIgnore
    private Set<Tournament> tournaments;


}
