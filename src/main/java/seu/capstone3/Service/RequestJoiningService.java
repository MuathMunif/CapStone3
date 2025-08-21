package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.DTOIN.RequestJoiningDTOIn;
import seu.capstone3.DTOOUT.RequestJoiningDTOOut;
import seu.capstone3.Model.Club;
import seu.capstone3.Model.Player;
import seu.capstone3.Model.RecruitmentOpportunity;
import seu.capstone3.Model.RequestJoining;
import seu.capstone3.Repository.ClubRepository;
import seu.capstone3.Repository.PlayerRepository;
import seu.capstone3.Repository.RecruitmentOpportunityRepository;
import seu.capstone3.Repository.RequestJoiningRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestJoiningService {
    private final RequestJoiningRepository requestJoiningRepository;
    private final RecruitmentOpportunityRepository recruitmentOpportunityRepository;
    private final PlayerRepository playerRepository;
    private final ClubRepository clubRepository;

    //todo check again the business
    public List<RequestJoining> getAllRequestJoining(){
        return requestJoiningRepository.findAll();
    }



    public void addRequestJoining(RequestJoiningDTOIn requestJoiningDTOIn){
        Player player = playerRepository.findPlayerById(requestJoiningDTOIn.getPlayer_id());
        RecruitmentOpportunity recruitmentOpportunity = recruitmentOpportunityRepository.findRecruitmentOpportunitiesById(requestJoiningDTOIn.getRecruitment_opportunity_id());
        if(player == null || recruitmentOpportunity == null){
            throw new ApiException("Player or recruitmentOpportunity not found");
        }
        if (!recruitmentOpportunity.getStatus().equals("OPEN")){
            throw new ApiException("RecruitmentOpportunity is CLOSED");
        }
        Club club = clubRepository.findClubById(recruitmentOpportunity.getClub().getId());
        if (!player.getCategory().equals(club.getCategory())){
            throw new ApiException("Player category does not match club category");
        }
        //todo check if player already has club ?
        RequestJoining requestJoining = new RequestJoining(null, "Pending", player, recruitmentOpportunity);
        requestJoiningRepository.save(requestJoining);
    }

    public List<RequestJoiningDTOOut> playerApplayedRequestJoining(Integer player_id){
        Player player = playerRepository.findPlayerById(player_id);
        if(player == null){
            throw new ApiException("Player not found");
        }
        List<RequestJoining> requestJoinings =
                requestJoiningRepository.findAllRequestJoiningByPlayer_Id(player_id);

        return requestJoinings.stream()
                .map(r -> new RequestJoiningDTOOut(
                        r.getId(),
                        r.getPlayer().getName(),
                        r.getStatus()
                ))
                .toList();
    }

}
