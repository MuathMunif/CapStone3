package seu.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seu.capstone3.Api.ApiResponse;
import seu.capstone3.Model.Tournament;
import seu.capstone3.Service.TournamentService;

@RestController
@RequestMapping("/api/v1/tournament")
@RequiredArgsConstructor
public class TournamentController {
    private final TournamentService tournamentService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllTournaments(){
        return ResponseEntity.status(200).body(tournamentService.getAllTournaments());
    }


    @PostMapping("/add/{sponsor_id}")
    public ResponseEntity<?> addTournament(@PathVariable Integer sponsor_id , @Valid @RequestBody Tournament tournament){
        tournamentService.addTournament(sponsor_id,tournament);
        return ResponseEntity.status(200).body(new ApiResponse("Tournament added successfully"));
    }

    @PutMapping("/update/{tournament_id}")
    public ResponseEntity<?> updateTournament(@PathVariable Integer tournament_id , @Valid @RequestBody Tournament tournament){
        tournamentService.updateTournament(tournament_id , tournament);
        return ResponseEntity.status(200).body(new ApiResponse("Tournament updated successfully"));
    }

    @DeleteMapping("/delete/{sponsor_id}/{tournament_id}")
    public ResponseEntity<?> deleteTournament(@PathVariable Integer tournament_id , @PathVariable Integer sponsor_id){
        tournamentService.deleteTournament(sponsor_id,tournament_id);
        return ResponseEntity.status(200).body(new ApiResponse("Tournament deleted successfully"));
    }
}
