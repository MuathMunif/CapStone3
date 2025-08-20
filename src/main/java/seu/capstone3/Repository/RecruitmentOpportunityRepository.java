package seu.capstone3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import seu.capstone3.Model.RecruitmentOpportunity;


import java.util.List;

@Repository
public interface RecruitmentOpportunityRepository extends JpaRepository<RecruitmentOpportunity, Integer> {
    RecruitmentOpportunity findRecruitmentOpportunitiesById(Integer id);

    List<RecruitmentOpportunity> findByClub_Id(Integer clubId);

    RecruitmentOpportunity findRecruitmentOpportunitiesByIdAndClub_Id(Integer id, Integer clubId);


}
