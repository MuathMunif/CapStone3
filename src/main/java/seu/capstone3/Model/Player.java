package seu.capstone3.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class Player {

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

    @NotNull(message = "The age must be not empty")
    @Positive(message = "The age must be a valid number")
    @Column(columnDefinition = "int not null")
    private Integer age;

    @NotEmpty(message = "The location must be not empty")
    @Column(columnDefinition = "varchar(20) not null")
    private String location;

    @NotNull(message = "The height must be not empty")
    @Positive(message = "The height must be a valid number")
    @Column(columnDefinition = "double not null")
    private Double height;

    @NotNull(message = "The weight must be not empty")
    @Positive(message = "The weight must be a valid number")
    @Column(columnDefinition = "double not null")
    private Double weight;

    @NotEmpty(message = "The description must be not empty")
    @Column(columnDefinition = "varchar(500) not null")
    private String description;

    @NotEmpty(message = "The skills must be not empty")
    @Column(columnDefinition = "varchar(500) not null")
    private String skills;

    @OneToMany(cascade = CascadeType.ALL , mappedBy = "player")
    @JsonIgnore
    private Set<RequestJoining> requestJoinings;

    @ManyToOne
    private Club club;

    @ManyToOne
    private Category category;

    @ManyToMany
    @JsonIgnore
    private Set<Tournament> tournaments;
}
