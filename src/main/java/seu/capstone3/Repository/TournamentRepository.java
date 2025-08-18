package seu.capstone3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import seu.capstone3.Model.Tournament;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {

    Tournament findTournamentById(Integer id);
}
