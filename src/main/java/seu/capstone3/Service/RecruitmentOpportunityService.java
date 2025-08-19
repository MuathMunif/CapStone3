package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.DTOIN.RecruitmentOpportunityDTO;
import seu.capstone3.DTOOUT.SimpleRecommendationResponse;
import seu.capstone3.Model.Club;
import seu.capstone3.Model.Player;
import seu.capstone3.Model.RecruitmentOpportunity;
import seu.capstone3.Model.RequestJoining;
import seu.capstone3.Repository.ClubRepository;
import seu.capstone3.Repository.PlayerRepository;
import seu.capstone3.Repository.RecruitmentOpportunityRepository;
import seu.capstone3.Repository.RequestJoiningRepository;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentOpportunityService {
    private final RecruitmentOpportunityRepository recruitmentOpportunityRepository;
    private final ClubRepository clubRepository;
    private final RequestJoiningRepository requestJoiningRepository;
    private final PlayerRepository playerRepository;
    private final AiScoutingService aiScoutingService;


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
        RecruitmentOpportunity recruitmentOpportunity = new RecruitmentOpportunity(null, recruitmentOpportunityDTO.getDescription() ,"OPEN", club ,null);
        recruitmentOpportunity.setStatus("OPEN");
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

    public void acceptPlayer(Integer recruitmentOpportunity_id, Integer requestJoining_id) {
        RecruitmentOpportunity recruitmentOpportunity = recruitmentOpportunityRepository.findRecruitmentOpportunitiesById(recruitmentOpportunity_id);
        RequestJoining requestJoining = requestJoiningRepository.findRequestJoiningById(requestJoining_id);

        if (recruitmentOpportunity == null) {
            throw new ApiException("Recruitment Opportunity not found");
        }
        if (requestJoining == null) {
            throw new ApiException("RequestJoining not found");
        }
        Club club = clubRepository.findClubById(recruitmentOpportunity.getClub().getId());
        Player player = playerRepository.findPlayerById(requestJoining.getPlayer().getId());

        requestJoining.setStatus("ACCEPTED");
        club.getPlayers().add(player);
        player.setClub(club);
        clubRepository.save(club);
        playerRepository.save(player);
        requestJoiningRepository.save(requestJoining);
    }


    //reject player
    public void rejectPlayer(Integer recruitmentOpportunity_id, Integer requestJoining_id) {
        RequestJoining requestJoining = requestJoiningRepository.findRequestJoiningById(requestJoining_id);
        RecruitmentOpportunity recruitmentOpportunity = recruitmentOpportunityRepository.findRecruitmentOpportunitiesById(recruitmentOpportunity_id);
        if (recruitmentOpportunity == null || requestJoining == null) {
            throw new ApiException("Recruitment Opportunity or request Joining not found");
        }
        requestJoining.setStatus("REJECTED");
        requestJoiningRepository.save(requestJoining);
    }


    public void closeRecruitmentOpportunity(Integer club_id , Integer recruitmentOpportunity_id) {
        Club club = clubRepository.findClubById(club_id);
        RecruitmentOpportunity recruitmentOpportunity = recruitmentOpportunityRepository.findRecruitmentOpportunitiesById(recruitmentOpportunity_id);
        if (club == null || recruitmentOpportunity == null) {
            throw new ApiException("Club or recruitment Opportunity not found");
        }
        if (!Objects.equals(club.getId(), recruitmentOpportunity.getClub().getId())) {
            throw new ApiException("You are not allowed to close this recruitment opportunity");
        }
        if (recruitmentOpportunity.getStatus().equals("CLOSED")) {
            throw new ApiException("Recruitment Opportunity is already closed");
        }

        recruitmentOpportunity.setStatus("CLOSED");
        recruitmentOpportunityRepository.save(recruitmentOpportunity);
    }
    public SimpleRecommendationResponse getAiRecommendations(Integer opportunityId) {
        RecruitmentOpportunity opp =
                recruitmentOpportunityRepository.findRecruitmentOpportunitiesById(opportunityId);

        if (opp == null) throw new RuntimeException("Recruitment Opportunity not found");

        List<RequestJoining> pendings =
                requestJoiningRepository.findAllByRecruitmentOpportunity_IdAndStatusIgnoreCase(
                        opportunityId, "PENDING");

        if (pendings == null || pendings.isEmpty()) {
            return new SimpleRecommendationResponse(
                    "No applicants yet.",
                    List.of(),
                    List.of(),
                    0
            );
        }

        // Always evaluate ALL applicants; category is handled in the prompt (mismatch gets lower score).
        List<Player> candidates = pendings.stream()
                .map(RequestJoining::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return aiScoutingService.recommend(opp, candidates);
    }
}
