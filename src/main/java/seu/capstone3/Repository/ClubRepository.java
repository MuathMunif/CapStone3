package seu.capstone3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import seu.capstone3.Model.Club;

@Repository
public interface ClubRepository  extends JpaRepository<Club, Integer> {

    Club findClubById(Integer id);

    Club findClubByEmail(String email);
}
