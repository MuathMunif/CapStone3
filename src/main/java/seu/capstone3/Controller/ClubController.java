package seu.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seu.capstone3.Api.ApiResponse;
import seu.capstone3.DTOIN.ClubDTO;
import seu.capstone3.Model.Club;
import seu.capstone3.Service.ClubService;

@RestController
@RequestMapping("/api/v1/club")
@RequiredArgsConstructor
public class ClubController {
    private final ClubService clubService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllClubs() {
        return ResponseEntity.status(200).body(clubService.getAllClubs());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addClub(@Valid @RequestBody ClubDTO clubDTO) {
        clubService.addClub(clubDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Club added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateClub(@PathVariable Integer id,@Valid @RequestBody Club club) {
        clubService.updateClub(id, club);
        return ResponseEntity.status(200).body(new ApiResponse("Club updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteClub(@PathVariable Integer id) {
        clubService.deleteClub(id);
        return ResponseEntity.status(200).body(new ApiResponse("Club deleted successfully"));
    }

    //Ex

    @GetMapping("/get-club-by-id/{id}")
    public ResponseEntity<?> getClubById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(clubService.getClubById(id));
    }
}
