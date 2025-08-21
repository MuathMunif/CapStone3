package seu.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seu.capstone3.Api.ApiResponse;
import seu.capstone3.DTOIN.ClubDTOIn;
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
    public ResponseEntity<?> addClub(@Valid @RequestBody ClubDTOIn clubDTOIn) {
        clubService.addClub(clubDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Club added successfully"));
    }

    @PutMapping("/update/{club_id}")
    public ResponseEntity<?> updateClub(@PathVariable Integer club_id,@Valid @RequestBody Club club) {
        clubService.updateClub(club_id, club);
        return ResponseEntity.status(200).body(new ApiResponse("Club updated successfully"));
    }

    @DeleteMapping("/delete/{club_id}")
    public ResponseEntity<?> deleteClub(@PathVariable Integer club_id) {
        clubService.deleteClub(club_id);
        return ResponseEntity.status(200).body(new ApiResponse("Club deleted successfully"));
    }

    //Ex

    @GetMapping("/get-club-by-id/{club_id}")
    public ResponseEntity<?> getClubById(@PathVariable Integer club_id) {
        return ResponseEntity.status(200).body(clubService.getClubById(club_id));
    }

    // dto
    @GetMapping("/get-club-by-id-dto/{club_id}")
    public ResponseEntity<?> getClubByIdDto(@PathVariable Integer club_id){
        return ResponseEntity.status(200).body(clubService.getClubByIdDto(club_id));
    }


    @GetMapping("/get-all-clubs-dto")
    public ResponseEntity<?> getAllClubsDto(){
        return ResponseEntity.status(200).body(clubService.getAllClubsDto());
    }


    @GetMapping("/get-all-club-by-location/{location}")
    public ResponseEntity<?> getAllClubsByLocation(@PathVariable String location){
        return ResponseEntity.status(200).body(clubService.getClubsByLocation(location));
    }


    @GetMapping("/get-all-club-by-category_id/{player_id}")
    public ResponseEntity<?> getAllClubsByCategoryId(@PathVariable Integer player_id){
        return ResponseEntity.status(200).body(clubService.getClubsByCategory(player_id));
    }

    @PostMapping("qualified-email/{recruitmentOpportunity_id}/{player_id}/{club_id}")
    public ResponseEntity<?>  sendQualifiedEmailToPlayer(@PathVariable Integer recruitmentOpportunity_id,@PathVariable Integer player_id,@PathVariable Integer club_id){
        clubService.sendQualifiedEmailToPlayer(recruitmentOpportunity_id, player_id, club_id);
        return ResponseEntity.status(200).body(new ApiResponse("Email sent successfully"));
    }
}
