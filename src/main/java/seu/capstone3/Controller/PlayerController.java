package seu.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seu.capstone3.Api.ApiResponse;
import seu.capstone3.DTOIN.PlayerDTO;
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
}
