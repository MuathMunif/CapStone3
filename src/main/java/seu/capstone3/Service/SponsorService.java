package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.Model.Sponsor;
import seu.capstone3.Repository.SponsorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SponsorService {

    private final SponsorRepository sponsorRepository;


    public List<Sponsor> getAllSponsors() {
        return sponsorRepository.findAll();
    }


    public void addSponsor(Sponsor sponsor) {
        Sponsor existingSponsor = sponsorRepository.findSponsorByEmail(sponsor.getEmail());
        if (existingSponsor != null) {
            throw new ApiException("Sponsor with this email "+sponsor.getEmail()+" already exist");
        }
        sponsorRepository.save(sponsor);
    }

    public void updateSponsor(Integer id ,Sponsor sponsor) {
        Sponsor sponsor1 = sponsorRepository.findSponsorById(id);
        if (sponsor1 == null) {
            throw new ApiException("Sponsor not found");
        }
        sponsor1.setName(sponsor.getName());
        sponsor1.setEmail(sponsor.getEmail());
        sponsor1.setPhoneNumber(sponsor.getPhoneNumber());
        sponsor1.setDescription(sponsor.getDescription());
        sponsorRepository.save(sponsor);
    }

    public void deleteSponsor(Integer id) {
        Sponsor sponsor = sponsorRepository.findSponsorById(id);
        if (sponsor == null) {
            throw new ApiException("Sponsor not found");
        }
        sponsorRepository.delete(sponsor);
    }
}
