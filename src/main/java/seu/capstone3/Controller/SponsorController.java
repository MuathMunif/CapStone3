package seu.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seu.capstone3.Api.ApiResponse;
import seu.capstone3.Model.Sponsor;
import seu.capstone3.Service.SponsorService;



@RestController
@RequestMapping("/api/v1/sponsor")
@RequiredArgsConstructor
public class SponsorController {
    private final SponsorService sponsorService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllSponsors() {
        return ResponseEntity.status(200).body(sponsorService.getAllSponsors());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSponsor(@Valid @RequestBody Sponsor sponsor) {
        sponsorService.addSponsor(sponsor);
        return ResponseEntity.status(200).body(new ApiResponse("Sponsor added successfully"));
    }


    @PutMapping("/update/{sponsor_id}")
    public ResponseEntity<?> updateSponsor(@PathVariable Integer sponsor_id , @Valid @RequestBody Sponsor sponsor) {
        sponsorService.updateSponsor(sponsor_id, sponsor);
        return ResponseEntity.status(200).body(new ApiResponse("Sponsor updated successfully"));
    }

    @DeleteMapping("/delete/{sponsor_id}")
    public ResponseEntity<?> deleteSponsor(@PathVariable Integer sponsor_id) {
        sponsorService.deleteSponsor(sponsor_id);
        return ResponseEntity.status(200).body(new ApiResponse("Sponsor deleted successfully"));
    }
}
