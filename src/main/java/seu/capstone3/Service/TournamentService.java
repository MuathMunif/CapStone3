package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.DTOIN.TournamentDTO;
import seu.capstone3.Model.*;
import seu.capstone3.Repository.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final SponsorRepository sponsorRepository;
    private final CategoryRepository categoryRepository;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;



    private void validatePlayersAndTeams(int numberOfPlayers, int numberOfTeams){
        if(numberOfPlayers % 2 != 0){
            throw new ApiException("Number of players must be even");
        }
        if(numberOfTeams <= 0){
            throw new ApiException("Number of teams must be at least 1");
        }
        if(numberOfTeams > numberOfPlayers){
            throw new ApiException("Number of teams cannot be greater than number of players");
        }
        if(numberOfPlayers % numberOfTeams != 0){
            throw new ApiException("Number of players must be divisible by number of teams");
        }
    }

    public List<Tournament> getAllTournaments(){
        return tournamentRepository.findAll();
    }


    public void addTournament(TournamentDTO tournamentDTO){
        Sponsor sponsor = sponsorRepository.findSponsorById(tournamentDTO.getSponsor_id());
        Category category = categoryRepository.findCategoryById(tournamentDTO.getCategory_id());
        if(sponsor == null || category == null){
            throw new ApiException("Sponsor not found or Category not found");
        }

        validatePlayersAndTeams(tournamentDTO.getNumberOfPlayers(),tournamentDTO.getNumberOfTeams());

        Tournament tournament = new Tournament(null ,tournamentDTO.getName(),
                tournamentDTO.getDescription(),
                tournamentDTO.getNumberOfPlayers(),
                tournamentDTO.getStartDate(),
                tournamentDTO.getEndDate(),
                tournamentDTO.getLocation(),
                sponsor,category,
                tournamentDTO.getNumberOfTeams(),
                null,
                null,
                0);
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

        validatePlayersAndTeams(tournament.getNumberOfPlayers(), tournament.getNumberOfTeams());


        oldTournament.setName(tournament.getName());
        oldTournament.setDescription(tournament.getDescription());
        oldTournament.setLocation(tournament.getLocation());
        oldTournament.setStartDate(tournament.getStartDate());
        oldTournament.setEndDate(tournament.getEndDate());
        oldTournament.setNumberOfTeams(tournament.getNumberOfTeams());
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

    public Set<Player> getPlayersInTournament(Integer tournamentId){
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);

        if (tournament == null) {
            throw new ApiException("Tournament not found");
        }
        return tournament.getPlayers();
    }


    public void determineTeamsRandomly(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);

        if (tournament == null) {
            throw new ApiException("Tournament not found");
        }

        if (!Objects.equals(tournament.getNumberOfPlayers(), tournament.getPlayerCounter())) {
            throw new ApiException("Cannot draw teams before tournament is full");
        }

        // تحقق إذا تم عمل القرعة مسبقًا
        if (!tournament.getTeams().isEmpty()) {
            throw new ApiException("Teams have already been drawn for this tournament");
        }

        List<Player> playersList = new ArrayList<>(tournament.getPlayers());
        Collections.shuffle(playersList); // خلط اللاعبين عشوائيًا

        int playersPerTeam = tournament.getNumberOfPlayers() / tournament.getNumberOfTeams();
        Set<Team> teams = new HashSet<>();

        for (int i = 0; i < tournament.getNumberOfTeams(); i++) {
            Team team = new Team();
            team.setTournament(tournament);
            team.setName("Team " + (i + 1));

            Set<Player> teamPlayers = new HashSet<>();
            for (int j = 0; j < playersPerTeam; j++) {
                Player player = playersList.get(i * playersPerTeam + j);
                player.setTeam(team); // ربط اللاعب بالفريق
                teamPlayers.add(player);
            }

            team.setPlayers(teamPlayers);
            teamRepository.save(team); // حفظ الفريق مع اللاعبين
            teams.add(team);
        }

        tournament.setTeams(teams);
        tournamentRepository.save(tournament); // حفظ البطولة مع الفرق
    }


    public Set<Team> getTeamsInTournament(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);
        if (tournament == null) {
            throw new ApiException("Tournament not found");
        }

        if (tournament.getTeams().isEmpty()) {
            throw new ApiException("Teams have not been drawn yet for this tournament");
        }

        return tournament.getTeams();
    }


}
