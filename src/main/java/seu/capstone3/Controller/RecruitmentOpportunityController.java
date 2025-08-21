package seu.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seu.capstone3.Api.ApiResponse;
import seu.capstone3.DTOIN.RecruitmentOpportunityDTOIn;
import seu.capstone3.Model.RecruitmentOpportunity;
import seu.capstone3.Service.RecruitmentOpportunityService;

@RestController
@RequestMapping("/api/v1/recruitment-opportunity")
@RequiredArgsConstructor
public class RecruitmentOpportunityController {
    private final RecruitmentOpportunityService recruitmentOpportunityService;


    @GetMapping("/get")
    public ResponseEntity<?> getAllRecruitmentOpportunities() {
        return ResponseEntity.status(200).body(recruitmentOpportunityService.getAllRecruitmentOpportunities());
    }


    @PostMapping("/add")
    public ResponseEntity<?> addRecruitmentOpportunity( @Valid @RequestBody RecruitmentOpportunityDTOIn recruitmentOpportunityDTOIn) {
        recruitmentOpportunityService.addRecruitmentOpportunity(recruitmentOpportunityDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Successfully added recruitment opportunity"));
    }


    @PutMapping("update/{recruitmentOpportunity_id}")
    public ResponseEntity<?> updateRecruitmentOpportunity(@PathVariable Integer recruitmentOpportunity_id , @Valid @RequestBody RecruitmentOpportunity recruitmentOpportunity) {
        recruitmentOpportunityService.updateRecruitmentOpportunity(recruitmentOpportunity_id, recruitmentOpportunity);
        return ResponseEntity.status(200).body(new ApiResponse("Successfully updated"));
    }


    @DeleteMapping("/delete/{club_id}/{recruitmentOpportunity_id}")
    public ResponseEntity<?> deleteRecruitmentOpportunity(@PathVariable Integer club_id , @PathVariable Integer recruitmentOpportunity_id) {
        recruitmentOpportunityService.deleteRecruitmentOpportunity(club_id, recruitmentOpportunity_id);
        return ResponseEntity.status(200).body(new ApiResponse("Successfully deleted"));
    }

    //Ex

    @PutMapping("/accept-player-to-club/{recruitmentOpportunity_id}/{requestJoining_id}")
    public ResponseEntity<?> acceptPlayer(@PathVariable Integer recruitmentOpportunity_id, @PathVariable Integer requestJoining_id) {
        recruitmentOpportunityService.acceptPlayer(recruitmentOpportunity_id, requestJoining_id);
        return ResponseEntity.status(200).body(new ApiResponse("Player accepted successfully"));
    }

    @PutMapping("/reject-player/{recruitmentOpportunity_id}/{requestJoining_id}")
    public ResponseEntity<?> rejectPlayer(@PathVariable Integer recruitmentOpportunity_id, @PathVariable Integer requestJoining_id) {
        recruitmentOpportunityService.rejectPlayer(recruitmentOpportunity_id, requestJoining_id);
        return ResponseEntity.status(200).body(new ApiResponse("Player rejected successfully"));
    }


    @PutMapping("/close-recruitment-opportunity/{club_id}/{recruitmentOpportunity_id}")
    public ResponseEntity<?> closeRecruitmentOpportunity(@PathVariable Integer club_id, @PathVariable Integer recruitmentOpportunity_id) {
        recruitmentOpportunityService.closeRecruitmentOpportunity(club_id, recruitmentOpportunity_id);
        return ResponseEntity.status(200).body(new ApiResponse("Recruitment opportunity closed"));
    }

    @PostMapping("/ai-recommendations/{opportunity_id}")
    public ResponseEntity<?> recommend(@PathVariable Integer opportunity_id) {
        return ResponseEntity.ok(recruitmentOpportunityService.getAiRecommendations(opportunity_id));
    }


    @GetMapping("/get-all-dto")
    public ResponseEntity<?> getAllRecruitmentOpportunitiesDto() {
        return ResponseEntity.status(200).body(recruitmentOpportunityService.getAllRecruitmentOpportunitiesDto());
    }


    @GetMapping("/get-all-by-club-id/{club_id}")
    public ResponseEntity<?> getAllRecruitmentOpportunitiesByClubId(@PathVariable Integer club_id) {
        return ResponseEntity.status(200).body(recruitmentOpportunityService.getOpportunitiesByClubId(club_id));
    }


    @GetMapping("/get-opportunities-by-Category_id/{Category_id}")
    public ResponseEntity<?> getOpportunitiesByCategoryId(@PathVariable Integer Category_id) {
        return ResponseEntity.status(200).body(recruitmentOpportunityService.getOpportunitiesByCategoryId(Category_id));
    }

}
