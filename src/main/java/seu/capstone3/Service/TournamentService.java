package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.DTOIN.TournamentDTO;
import seu.capstone3.DTOOUT.PlayerTournamentOutDTO;
import seu.capstone3.DTOOUT.TournamentOutDTO;
import seu.capstone3.Model.*;
import seu.capstone3.Repository.*;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final SponsorRepository sponsorRepository;
    private final CategoryRepository categoryRepository;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final EmailService emailService;



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
                "OPEN",
                0);

        tournamentRepository.save(tournament);
    }

    //todo to be used TournamentDTO
    public void updateTournament(Integer tournamentId, Integer sponsor_id, Tournament tournament) {
        Tournament oldTournament = tournamentRepository.findTournamentById(tournamentId);
        if (oldTournament == null) {
            throw new ApiException("Tournament not found");
        }

        if (!Objects.equals(sponsor_id, oldTournament.getSponsor().getId())) {
            throw new ApiException("You are not allowed to update this tournament");
        }

        oldTournament.setName(tournament.getName());
        oldTournament.setDescription(tournament.getDescription());
        oldTournament.setLocation(tournament.getLocation());
        oldTournament.setStartDate(tournament.getStartDate());
        oldTournament.setEndDate(tournament.getEndDate());
        oldTournament.setNumberOfTeams(tournament.getNumberOfTeams());

        validatePlayersAndTeams(tournament.getNumberOfPlayers(), tournament.getNumberOfTeams());

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


    public List<TournamentOutDTO> getAllTournamentsOutDTO(){
        List<Tournament> tournaments = tournamentRepository.findAll();
        List<TournamentOutDTO> tournamentOutDTOS = new ArrayList<>();

        for(Tournament t: tournaments){
            TournamentOutDTO tournamentOutDTO = new TournamentOutDTO();
            tournamentOutDTO.setName(t.getName());
            tournamentOutDTO.setStartDate(t.getStartDate());
            tournamentOutDTO.setEndDate(t.getEndDate());
            tournamentOutDTO.setLocation(t.getLocation());
            tournamentOutDTO.setCategoryName(t.getCategory().getName());
            tournamentOutDTO.setStatus(t.getStatus());

            tournamentOutDTOS.add(tournamentOutDTO);
        }
        return tournamentOutDTOS;
    }


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

    public void assignPlayerToTournament(Integer tournamentId, Integer playerId) {
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);
        Player player = playerRepository.findPlayerById(playerId);

        if (tournament == null || player == null) {
            throw new ApiException("Tournament or player not found");
        }

        if (!player.getCategory().equals(tournament.getCategory())) {
            throw new ApiException("You are not allowed to assign a different Category tournament");
        }

        if (Objects.equals(tournament.getNumberOfPlayers(), tournament.getPlayerCounter())) {
            throw new ApiException("Sorry, the tournament is full");
        }

        if (tournament.getPlayers().contains(player)) {
            throw new ApiException("Player is already registered in this tournament");
        }

        if(tournament.getStatus().equals("CLOSED")){
            throw new ApiException("Sorry, tournament closed");
        }


        for (Tournament t : player.getTournaments()) {
            boolean overlap = !(tournament.getEndDate().isBefore(t.getStartDate()) || tournament.getStartDate().isAfter(t.getEndDate()));
            if (overlap) {
                throw new ApiException("Player is already registered in another tournament that overlaps in time");
            }
        }

        tournament.getPlayers().add(player);
        tournament.setPlayerCounter(tournament.getPlayerCounter() + 1);

        if (player.getTournaments() == null) {
            player.setTournaments(new HashSet<>());
        }
        if (Objects.equals(tournament.getPlayerCounter(), tournament.getNumberOfPlayers())) {
            tournament.setStatus("FULL");
        }

        player.getTournaments().add(tournament);

        playerRepository.save(player);
        tournamentRepository.save(tournament);
        emailService.sendTournamentWelcome(tournament, player);


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
        //يستخدم هاش لتخزين العناصر بسرعة والوصول إليها بسرعة.
        //ترتيب العناصر داخل HashSet غير مضمون، أي لن يكون بالضرورة نفس ترتيب الإضافة.


        for (int i = 0; i < tournament.getNumberOfTeams(); i++) {
            Team team = new Team();
            team.setTournament(tournament);
            team.setName("Team " + (i + 1));

            Set<Player> teamPlayers = new HashSet<>();
            for (int j = 0; j < playersPerTeam; j++) {
                Player player = playersList.get(i * playersPerTeam + j);
                player.setTeam(team);
                teamPlayers.add(player);
            }

            team.setPlayers(teamPlayers);
            teamRepository.save(team);
            teams.add(team);
        }

        tournament.setTeams(teams);
        tournamentRepository.save(tournament);
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


 public List<PlayerTournamentOutDTO> getPlayerTournament(String email){
        Player player = playerRepository.findPlayerByEmail(email);

     if (player == null) {
         throw new ApiException("Player not found");
     }

     List<PlayerTournamentOutDTO> result = new ArrayList<>();

     for(Tournament t: player.getTournaments()){
         PlayerTournamentOutDTO tournamentOutDTO = new PlayerTournamentOutDTO(
                 t.getName(),t.getStartDate().toString(),t.getEndDate().toString(),
                 t.getSponsor().getName()
         );
         result.add(tournamentOutDTO);

     }
     return result;
 }

 public List<TournamentOutDTO> getOpenTournaments(){

     List<Tournament> openTournaments = tournamentRepository.findOpenTournaments();
     List<TournamentOutDTO> dtoList = new ArrayList<>();

    for(Tournament t: openTournaments){
        TournamentOutDTO dto = new TournamentOutDTO();
        dto.setName(t.getName());
        dto.setStartDate(t.getStartDate());
        dto.setEndDate(t.getEndDate());
        dto.setLocation(t.getLocation());
        dto.setCategoryName(t.getCategory().getName());
        dto.setStatus(t.getStatus());
        dtoList.add(dto);
    }
    return dtoList;
 }

 public void closeTournament(Integer sponsorId, Integer tournamentId){

        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);

     if (tournament == null) {
         throw new ApiException("Tournament not found");
     }
     if(!Objects.equals(tournament.getSponsor().getId(), sponsorId)){
         throw new ApiException("You are not allowed to close this tournament");
     }

     tournament.setStatus("CLOSED");
     tournamentRepository.save(tournament);
 }


 public Integer remainingPlayersToFull(Integer tournamentId){
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);

     if (tournament == null) {
         throw new ApiException("Tournament not found");
     }

     return tournament.getNumberOfPlayers() - tournament.getPlayerCounter();
 }


    public void withdrawPlayerFromTournament(Integer tournamentId, Integer playerId) {
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);
        Player player = playerRepository.findPlayerById(playerId);

        if (tournament == null || player == null) {
            throw new ApiException("Tournament or player not found");
        }

        if (!tournament.getPlayers().contains(player)) {
            throw new ApiException("Player is not registered in this tournament");
        }

        long daysUntilTournament = ChronoUnit.DAYS.between(java.time.LocalDate.now(), tournament.getStartDate());

        if (daysUntilTournament < 2) {
            throw new ApiException("You cannot withdraw, less than 2 days remaining until the tournament");
        }

        tournament.getPlayers().remove(player);
        tournament.setPlayerCounter(tournament.getPlayerCounter() - 1);

        if (player.getTournaments() != null) {
            player.getTournaments().remove(tournament);
        }

        if (tournament.getStatus().equals("FULL")) {
            tournament.setStatus("OPEN");
        }

        playerRepository.save(player);
        tournamentRepository.save(tournament);
    }


    public List<TournamentOutDTO> getTournamentsByCategory(Integer categoryId){
        Category category = categoryRepository.findCategoryById(categoryId);

        if (category == null) {
            throw new ApiException("There is no tournaments");
        }

        List<Tournament> tournaments = tournamentRepository.findAllByCategory(categoryId);
        List<TournamentOutDTO> dtoOutList = new ArrayList<>();

        for(Tournament t: tournaments){
            TournamentOutDTO dtoOut = new TournamentOutDTO(
                    t.getName(),
                    t.getStartDate(),
                    t.getEndDate(),
                    t.getLocation(),
                    t.getCategory().getName(),
                    t.getStatus()
            );
            dtoOutList.add(dtoOut);
        }
        return dtoOutList;
    }

    public List<TournamentOutDTO> getTournamentsBySponsorId(Integer sponsorId){

        Sponsor sponsor = sponsorRepository.findSponsorById(sponsorId);

        if (sponsor == null) {
            throw new ApiException("There is no tournaments");
        }

        List<Tournament> tournaments = tournamentRepository.findTournamentsBySponsorId(sponsorId);
        List<TournamentOutDTO> dtoOutList = new ArrayList<>();

        for(Tournament t: tournaments){
            TournamentOutDTO dtoOut = new TournamentOutDTO(
                    t.getName(),
                    t.getStartDate(),
                    t.getEndDate(),
                    t.getLocation(),
                    t.getCategory().getName(),
                    t.getStatus()
            );

            dtoOutList.add(dtoOut);
        }

        return dtoOutList;

    }
}
