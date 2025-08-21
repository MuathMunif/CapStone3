package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.DTOIN.RecruitmentOpportunityDTOIn;
import seu.capstone3.DTOOUT.PlayerAvailableOpportunityDTOOut;
import seu.capstone3.DTOOUT.RecruitmentOpportunityDTOOut;
import seu.capstone3.DTOOUT.RequestJoiningDTOOut;
import seu.capstone3.DTOOUT.SimpleRecommendationResponseDTOOut;
import seu.capstone3.Model.Club;
import seu.capstone3.Model.Player;
import seu.capstone3.Model.RecruitmentOpportunity;
import seu.capstone3.Model.RequestJoining;
import seu.capstone3.Repository.ClubRepository;
import seu.capstone3.Repository.PlayerRepository;
import seu.capstone3.Repository.RecruitmentOpportunityRepository;
import seu.capstone3.Repository.RequestJoiningRepository;

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
    private final EmailService emailService;


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

    public void addRecruitmentOpportunity(RecruitmentOpportunityDTOIn recruitmentOpportunityDTOIn) {
        Club club = clubRepository.findClubById(recruitmentOpportunityDTOIn.getClub_id());
        if (club == null) {
            throw new ApiException("Club not found");
        }
        RecruitmentOpportunity recruitmentOpportunity = new RecruitmentOpportunity(null, recruitmentOpportunityDTOIn.getTitle(), recruitmentOpportunityDTOIn.getDescription() ,"OPEN", club ,null);
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

        emailService.sendAcceptedEmail(player, club);
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
        emailService.sendRejectedEmail(requestJoining.getPlayer(),recruitmentOpportunity.getClub());
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
    public SimpleRecommendationResponseDTOOut getAiRecommendations(Integer opportunityId) {
        RecruitmentOpportunity opp =
                recruitmentOpportunityRepository.findRecruitmentOpportunitiesById(opportunityId);

        if (opp == null) throw new RuntimeException("Recruitment Opportunity not found");

        List<RequestJoining> pending =
                requestJoiningRepository.findAllByRecruitmentOpportunity_IdAndStatusIgnoreCase(
                        opportunityId, "PENDING");

        if (pending == null || pending.isEmpty()) {
            return new SimpleRecommendationResponseDTOOut(
                    "No applicants yet.",
                    List.of(),
                    List.of(),
                    0
            );
        }
        // Always evaluate ALL applicants; category is handled in the prompt (mismatch gets lower score).
        List<Player> candidates = pending.stream()
                .map(RequestJoining::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return aiScoutingService.recommend(opp, candidates);
    }



    // this method to convert to dto
    public RecruitmentOpportunityDTOOut convertToDTO(RecruitmentOpportunity opportunity) {
        RecruitmentOpportunityDTOOut dto = new RecruitmentOpportunityDTOOut();
        dto.setClubName(opportunity.getClub() != null ? opportunity.getClub().getName() : null);
        dto.setDescription(opportunity.getDescription());
        dto.setStatus(opportunity.getStatus());
        dto.setTitle(opportunity.getTitle());


        if (opportunity.getRequestJoinings() != null) {
            dto.setRequests(
                    opportunity.getRequestJoinings().stream()
                            .map(req -> new RequestJoiningDTOOut(
                                    req.getId(),
                                    req.getPlayer().getName(),
                                    req.getStatus()
                            ))
                            .toList()
            );
        }
        return dto;
    }


    // get all Recruitment Opportunities with dto
    public List<RecruitmentOpportunityDTOOut> getAllRecruitmentOpportunitiesDto() {
        List<RecruitmentOpportunity> opportunities = recruitmentOpportunityRepository.findAll();
        return opportunities.stream()
                .map(this::convertToDTO)
                .toList();
    }


    // get all Recruitment Opportunities for one club
    public List<RecruitmentOpportunityDTOOut> getOpportunitiesByClubId(Integer clubId) {
        List<RecruitmentOpportunity> opportunities = recruitmentOpportunityRepository.findByClub_Id(clubId);
        if (opportunities.isEmpty()) {
            throw new ApiException("No opportunities found for this club");
        }
        return opportunities.stream()
                .map(this::convertToDTO)
                .toList();
    }


    // find Open Recruitment Opportunities by category Id
    public List<PlayerAvailableOpportunityDTOOut> getOpportunitiesByCategoryId(Integer categoryId) {
        List<RecruitmentOpportunity> opportunities = recruitmentOpportunityRepository.findRecruitmentOpportunitiesByStatusAndClub_CategoryId("OPEN",categoryId);
        if (opportunities.isEmpty()) {
            throw new ApiException("No opportunities found for this category");
        }
        return opportunities.stream()
                .map(o -> new PlayerAvailableOpportunityDTOOut(
                        o.getClub().getName()
                        ,o.getTitle()
                        ,o.getDescription()
                        ,o.getStatus()))
                .toList();
    }
}
