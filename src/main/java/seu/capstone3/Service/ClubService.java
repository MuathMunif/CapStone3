package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.Model.Club;
import seu.capstone3.Repository.ClubRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;


    public List<Club> getAllClubs(){
        return clubRepository.findAll();
    }


    public void addClub(Club club){
        clubRepository.save(club);
    }

    public void updateClub(Integer id ,Club club){
        Club oldClub = clubRepository.findClubById(id);
        if(oldClub == null){
            throw new ApiException("Club not found");
        }
        oldClub.setName(club.getName());
        oldClub.setEmail(club.getEmail());
        oldClub.setPhoneNumber(oldClub.getPhoneNumber());
        oldClub.setLocation(oldClub.getLocation());
        clubRepository.save(oldClub);
    }

    public void deleteClub(Integer id){
        Club club = clubRepository.findClubById(id);
        if(club == null){
            throw new ApiException("Club not found");
        }
        clubRepository.delete(club);
    }
}
