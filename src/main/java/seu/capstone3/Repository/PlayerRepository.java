package seu.capstone3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seu.capstone3.Model.Player;

public interface PlayerRepository extends JpaRepository<Player, Integer> {

    Player findPlayerById(Integer id);
}
