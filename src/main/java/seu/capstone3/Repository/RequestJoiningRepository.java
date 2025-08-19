package seu.capstone3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import seu.capstone3.Model.RequestJoining;

import java.util.List;

@Repository
public interface RequestJoiningRepository extends JpaRepository<RequestJoining, Integer> {
    RequestJoining findRequestJoiningById(Integer id);

    List<RequestJoining> findAllByRecruitmentOpportunity_IdAndStatusIgnoreCase(Integer opportunityId, String status);
}
