package seu.capstone3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import seu.capstone3.Model.Sponsor;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Integer> {

    Sponsor findSponsorById(Integer id);

    Sponsor findSponsorByEmail(String email);
}
