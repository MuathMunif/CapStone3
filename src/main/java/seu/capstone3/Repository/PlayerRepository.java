package seu.capstone3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import seu.capstone3.Model.Category;
import seu.capstone3.Model.Club;
import seu.capstone3.Model.Player;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Integer> {

    Player findPlayerById(Integer id);

    Player findPlayerByEmail(String email);

    @Query("SELECT p FROM Player p WHERE p.club IS NULL")
    List<Player> getPlayersWithoutClub();



}
