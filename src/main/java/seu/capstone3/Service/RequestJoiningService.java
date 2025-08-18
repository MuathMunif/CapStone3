package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.Model.RecruitmentOpportunity;
import seu.capstone3.Model.RequestJoining;
import seu.capstone3.Repository.RecruitmentOpportunityRepository;
import seu.capstone3.Repository.RequestJoiningRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestJoiningService {
    private final RequestJoiningRepository requestJoiningRepository;
    private final RecruitmentOpportunityRepository recruitmentOpportunityRepository;

    //todo check again the business
//    public List<RequestJoining> getAllRequestJoining(){
//        return requestJoiningRepository.findAll();
//    }
//


    public void addRequestJoining(RequestJoining requestJoining){
        RecruitmentOpportunity recruitmentOpportunity = recruitmentOpportunityRepository.findRecruitmentOpportunitiesById(requestJoining.getRecruitmentOpportunity().getId());
        if(recruitmentOpportunity == null){
            throw new ApiException("Recruitment Opportunity Not Found");
        }
        requestJoining.setStatus("PENDING");
        requestJoiningRepository.save(requestJoining);
    }

}
