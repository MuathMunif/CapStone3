package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.Model.Sponsor;
import seu.capstone3.Model.Tournament;
import seu.capstone3.Repository.SponsorRepository;
import seu.capstone3.Repository.TournamentRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final SponsorRepository sponsorRepository;


    public List<Tournament> getAllTournaments(){
        return tournamentRepository.findAll();
    }

    public void addTournament(Integer sponsorId ,Tournament tournament){
        Sponsor sponsor = sponsorRepository.findSponsorById(sponsorId);
        if (sponsor == null) {
            throw new ApiException("You are not allowed to create a tournament");
        }
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
}
