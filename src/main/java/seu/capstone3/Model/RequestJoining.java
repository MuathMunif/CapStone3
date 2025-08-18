package seu.capstone3.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
public class RequestJoining {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Pattern(regexp = "^(PENDING|ACCEPTED|REJECTED)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "status must be PENDING, ACCEPTED, or REJECTED (case-insensitive)")
    private String status;

    @ManyToOne
    private Player player;

    @ManyToOne
    @JsonIgnore
    private RecruitmentOpportunity recruitmentOpportunity;




}
