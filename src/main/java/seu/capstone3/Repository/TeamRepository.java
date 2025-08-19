package seu.capstone3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import seu.capstone3.Model.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team,Integer> {
}
