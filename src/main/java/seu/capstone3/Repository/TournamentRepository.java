package seu.capstone3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import seu.capstone3.DTOOUT.TournamentOutDTO;
import seu.capstone3.Model.Tournament;

import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {

    Tournament findTournamentById(Integer id);

    @Query("SELECT t FROM Tournament t WHERE t.status = 'OPEN'")
    List<Tournament> findOpenTournaments();

    @Query("select t from Tournament t where t.category.id=?1 ")
    List<Tournament> findAllByCategory(Integer categoryId);

    @Query("select t from Tournament t where t.sponsor.id=?1 ")
    List<Tournament> findTournamentsBySponsorId(Integer sponsorId);


}
