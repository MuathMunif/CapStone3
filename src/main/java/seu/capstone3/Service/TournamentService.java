package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.DTOIN.TournamentDTO;
import seu.capstone3.Model.Category;
import seu.capstone3.Model.Player;
import seu.capstone3.Model.Sponsor;
import seu.capstone3.Model.Tournament;
import seu.capstone3.Repository.CategoryRepository;
import seu.capstone3.Repository.PlayerRepository;
import seu.capstone3.Repository.SponsorRepository;
import seu.capstone3.Repository.TournamentRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final SponsorRepository sponsorRepository;
    private final CategoryRepository categoryRepository;
    private final PlayerRepository playerRepository;


    public List<Tournament> getAllTournaments(){
        return tournamentRepository.findAll();
    }

//    public void addTournament(Integer sponsorId ,Tournament tournament){
//        Sponsor sponsor = sponsorRepository.findSponsorById(sponsorId);
//        if (sponsor == null) {
//            throw new ApiException("You are not allowed to create a tournament");
//        }
//        tournament.setSponsor(sponsor);
//        tournamentRepository.save(tournament);
//    }


    public void addTournament(TournamentDTO tournamentDTO){
        Sponsor sponsor = sponsorRepository.findSponsorById(tournamentDTO.getSponsor_id());
        Category category = categoryRepository.findCategoryById(tournamentDTO.getCategory_id());
        if(sponsor == null || category == null){
            throw new ApiException("Sponsor not found or Category not found");
        }
        Tournament tournament = new Tournament(null ,tournamentDTO.getName(),tournamentDTO.getDescription(),tournamentDTO.getNumberOfPlayers(),tournamentDTO.getStartDate(),tournamentDTO.getEndDate(),tournamentDTO.getLocation(),sponsor,category,null,0);
        tournamentRepository.save(tournament);
    }

    public void updateTournament(Integer tournamentId ,Tournament tournament){
        Tournament oldTournament = tournamentRepository.findTournamentById(tournamentId);
        if (oldTournament == null) {
            throw new ApiException("Tournament not found");
        }
        if (!Objects.equals(tournament.getSponsor().getId(), oldTournament.getSponsor().getId())) {
            throw new ApiException("You are not allowed to update a tournament");
        }
        oldTournament.setName(tournament.getName());
        oldTournament.setDescription(tournament.getDescription());
        oldTournament.setLocation(tournament.getLocation());
        oldTournament.setStartDate(tournament.getStartDate());
        oldTournament.setEndDate(tournament.getEndDate());
        tournamentRepository.save(oldTournament);
    }

    public void deleteTournament(Integer sponsorId ,Integer tournamentId){
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);
        if (tournament == null) {
            throw new ApiException("Tournament not found");
        }
        Sponsor sponsor = sponsorRepository.findSponsorById(sponsorId);
        if (!Objects.equals(tournament.getSponsor().getId(), sponsor.getId())) {
            throw new ApiException("You are not allowed to delete a tournament");
        }
    }

    // EX

    public void assignPlayerToTournament(Integer tournamentId ,Integer playerId){
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);
        Player player = playerRepository.findPlayerById(playerId);
        if (tournament == null || player == null) {
            throw new ApiException("Tournament or player not found");
        }
        if (!player.getCategory().equals(tournament.getCategory())) {
            throw new ApiException("You are not allowed to assign a  a different Category tournament ");
        }// todo check the player if already assign this tournament
        if (Objects.equals(tournament.getNumberOfPlayers(), tournament.getPlayerCounter())){
            throw new ApiException("Sorry The tournament is full , join another tournament");
        }
        tournament.getPlayers().add(player);
        tournament.setPlayerCounter(tournament.getPlayerCounter() + 1);
        tournamentRepository.save(tournament);
    }

}
