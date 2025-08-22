package seu.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import seu.capstone3.Api.ApiResponse;
import seu.capstone3.DTOIN.PlayerDTOIn;
import seu.capstone3.Model.Player;
import seu.capstone3.Service.PlayerService;

@RestController
@RequestMapping("/api/v1/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllPlayers() {
        return ResponseEntity.status(200).body(playerService.getAllPlayers());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPlayer(@Valid @RequestBody PlayerDTOIn playerDTOIn) {
        playerService.addPlayer(playerDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Player added successfully"));
    }


    @PutMapping("/update/{player_id}")
    public ResponseEntity<?> updatePlayer(@PathVariable Integer player_id, @Valid @RequestBody Player player) {
        playerService.updatePlayer(player_id, player);
        return ResponseEntity.status(200).body(new ApiResponse("Player updated successfully"));
    }

    @DeleteMapping("/delete/{player_id}")
    public ResponseEntity<?> deletePlayer(@PathVariable Integer player_id) {
        playerService.deletePlayer(player_id);
        return ResponseEntity.status(200).body(new ApiResponse("Player deleted successfully"));
    }

    //Ex

    @PostMapping("/upload-cv/{player_id}")
    public ResponseEntity<?> uploadCv(@PathVariable Integer player_id, @RequestParam("cv") MultipartFile file) {
        try {
            playerService.uploadCv(player_id, file);
            return ResponseEntity.status(200).body(new ApiResponse("CV uploaded successfully"));
        }
        catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }


    @GetMapping("/get-player-by-id/{player_id}")
    public ResponseEntity<?> getPlayer(@PathVariable Integer player_id) {
        try {
            return ResponseEntity.status(200).body(playerService.getPlayerWithCv(player_id));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
        }
    }


    @GetMapping("/get-all-players-without-club")
    public ResponseEntity<?> getAllPlayersWithOutClub(){
        return ResponseEntity.status(200).body(playerService.getPlayersWithoutClub());
    }


    @GetMapping("/get-all-player-dto")
    public ResponseEntity<?> getAllPlayerDTO(){
        playerService.getAllPlayersDto();
        return ResponseEntity.status(200).body(playerService.getAllPlayersDto());
    }

    @GetMapping("/strengths-weaknesses/{player_id}")
    public ResponseEntity<?> analyzeStrengthsWeaknesses(@PathVariable Integer player_id) {
        return ResponseEntity.status(200).body(playerService.analyzePlayerStrengthsWeaknesses(player_id));
    }

    @GetMapping("/training-plan/{player_id}/{days}")
    public ResponseEntity<?> trainingPlan(@PathVariable Integer player_id,@PathVariable Integer days){
        return ResponseEntity.status(200).body(playerService.getTrainingPlanSimpleDto(player_id,days));
    }
}
