package seu.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import seu.capstone3.Api.ApiResponse;
import seu.capstone3.DTOIN.PlayerDTO;
import seu.capstone3.DTOOUT.PlayerSWAnalysisDTO;
import seu.capstone3.Model.Player;
import seu.capstone3.Service.AiScoutingService;
import seu.capstone3.Service.PlayerService;

@RestController
@RequestMapping("/api/v1/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    private final AiScoutingService aiScoutingService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllPlayers() {
        return ResponseEntity.status(200).body(playerService.getAllPlayers());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPlayer(@Valid @RequestBody PlayerDTO playerDTO) {
        playerService.addPlayer(playerDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Player added successfully"));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePlayer(@PathVariable Integer id, @Valid @RequestBody Player player) {
        playerService.updatePlayer(id, player);
        return ResponseEntity.status(200).body(new ApiResponse("Player updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable Integer id) {
        playerService.deletePlayer(id);
        return ResponseEntity.status(200).body(new ApiResponse("Player deleted successfully"));
    }

    //Ex

    @PostMapping("/upload-cv/{id}")
    public ResponseEntity<String> uploadCv(@PathVariable Integer id, @RequestParam("cv") MultipartFile file) {
        try {
            playerService.uploadCv(id, file);
            return ResponseEntity.status(200).body("CV uploaded successfully");
        }
        catch (Exception e) { //todo check
            return ResponseEntity.status(500).body(e.getMessage());
        }

    }


    @GetMapping("/get-player-by-id/{id}")
    public ResponseEntity<?> getPlayer(@PathVariable Integer id) {
        try {
            return ResponseEntity.status(200).body(playerService.getPlayerWithCv(id));
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

    @GetMapping("/strengths-weaknesses/{id}")
    public ResponseEntity<PlayerSWAnalysisDTO> analyzeStrengthsWeaknesses(@PathVariable Integer id) {
        Player player = playerService.getPlayerById(id);
        PlayerSWAnalysisDTO result = aiScoutingService.analyzePlayerStrengthsWeaknesses(player);
        return ResponseEntity.ok(result);
    }
}
