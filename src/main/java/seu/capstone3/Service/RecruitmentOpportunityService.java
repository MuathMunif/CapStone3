package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.DTOIN.RecruitmentOpportunityDTO;
import seu.capstone3.Model.Club;
import seu.capstone3.Model.RecruitmentOpportunity;
import seu.capstone3.Repository.ClubRepository;
import seu.capstone3.Repository.RecruitmentOpportunityRepository;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecruitmentOpportunityService {
    private final RecruitmentOpportunityRepository recruitmentOpportunityRepository;
    private final ClubRepository clubRepository;


    public List<RecruitmentOpportunity> getAllRecruitmentOpportunities() {
        return recruitmentOpportunityRepository.findAll();
    }


//    //todo create DTO
//    public void addRecruitmentOpportunity (Integer club_id , RecruitmentOpportunity recruitmentOpportunity) {
//        Club club = clubRepository.findClubById(club_id);
//        if (club == null) {
//            throw new ApiException("Club not found");
//        }
//        recruitmentOpportunity.setClub(club);
//        recruitmentOpportunityRepository.save(recruitmentOpportunity);
//    }

    public void addRecruitmentOpportunity(RecruitmentOpportunityDTO recruitmentOpportunityDTO) {
        Club club = clubRepository.findClubById(recruitmentOpportunityDTO.getClub_id());
        if (club == null) {
            throw new ApiException("Club not found");
        }
        RecruitmentOpportunity recruitmentOpportunity = new RecruitmentOpportunity(null, recruitmentOpportunityDTO.getDescription() , club ,null);
        recruitmentOpportunityRepository.save(recruitmentOpportunity);
    }


    public void updateRecruitmentOpportunity(Integer id, RecruitmentOpportunity recruitmentOpportunity) {
        RecruitmentOpportunity oldRecruitmentOpportunity1 = recruitmentOpportunityRepository.findRecruitmentOpportunitiesById(id);
        if (oldRecruitmentOpportunity1 == null) {
            throw new ApiException("Recruitment Opportunity not found");
        }
        if (!Objects.equals(oldRecruitmentOpportunity1.getClub().getId(), recruitmentOpportunity.getClub().getId())) {
            throw new ApiException("You are not allowed to update this recruitment opportunity");
        }
        oldRecruitmentOpportunity1.setDescription(recruitmentOpportunity.getDescription());
        recruitmentOpportunityRepository.save(oldRecruitmentOpportunity1);
    }


    public void deleteRecruitmentOpportunity(Integer club_id , Integer recruitment_opportunity_id) {
        Club club = clubRepository.findClubById(club_id);
        RecruitmentOpportunity recruitmentOpportunity = recruitmentOpportunityRepository.findRecruitmentOpportunitiesById(recruitment_opportunity_id);

        if (club == null) {
            throw new ApiException("Club not found");
        }
        if (!Objects.equals(club.getId(), recruitmentOpportunity.getClub().getId())) {
            throw new ApiException("You are not allowed to delete this recruitment opportunity");
        }
        recruitmentOpportunityRepository.delete(recruitmentOpportunity);
    }
}
