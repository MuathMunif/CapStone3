package seu.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seu.capstone3.Api.ApiResponse;
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


    @PostMapping("/add/{club_id}")
    public ResponseEntity<?> addRecruitmentOpportunity(@PathVariable Integer club_id , @Valid @RequestBody RecruitmentOpportunity recruitmentOpportunity) {
        recruitmentOpportunityService.addRecruitmentOpportunity(club_id, recruitmentOpportunity);
        return ResponseEntity.status(200).body(new ApiResponse("Successfully added recruitment opportunity"));
    }


    @PutMapping("update/{id}")
    public ResponseEntity<?> updateRecruitmentOpportunity(@PathVariable Integer id , @Valid @RequestBody RecruitmentOpportunity recruitmentOpportunity) {
        recruitmentOpportunityService.updateRecruitmentOpportunity(id, recruitmentOpportunity);
        return ResponseEntity.status(200).body(new ApiResponse("Successfully updated"));
    }


    @DeleteMapping("/delete/{club_id}/{id}")
    public ResponseEntity<?> deleteRecruitmentOpportunity(@PathVariable Integer club_id , @PathVariable Integer id) {
        recruitmentOpportunityService.deleteRecruitmentOpportunity(club_id, id);
        return ResponseEntity.status(200).body(new ApiResponse("Successfully deleted"));
    }
}
