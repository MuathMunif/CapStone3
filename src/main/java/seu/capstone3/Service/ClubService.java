package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.DTOIN.ClubDTO;
import seu.capstone3.Model.Category;
import seu.capstone3.Model.Club;
import seu.capstone3.Repository.CategoryRepository;
import seu.capstone3.Repository.ClubRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final CategoryRepository categoryRepository;


    public List<Club> getAllClubs(){
        return clubRepository.findAll();
    }


//    public void addClub(Club club){
//        Category category = categoryRepository.findCategoryById(club.getCategory().getId());
//        if (category == null){
//            throw new ApiException("Category not found");
//        }
//        club.setCategory(category);
//        clubRepository.save(club);
//    }

    public void addClub(ClubDTO clubDTO){
        Category category = categoryRepository.findCategoryById(clubDTO.getCategory_id());
        if(category == null){
            throw new ApiException("Category not found");
        }
        Club club = new Club(null , clubDTO.getCr(),clubDTO.getName(),clubDTO.getEmail(),clubDTO.getPhoneNumber(),clubDTO.getLocation(),null,null,category);
        clubRepository.save(club);
    }

    public void updateClub(Integer id ,Club club){
        Club oldClub = clubRepository.findClubById(id);
        Category category = categoryRepository.findCategoryById(club.getCategory().getId());
        if(oldClub == null){
            throw new ApiException("Club not found");
        }
        if (category == null){
            throw new ApiException("Category not found");
        }
        oldClub.setName(club.getName());
        oldClub.setEmail(club.getEmail());
        oldClub.setPhoneNumber(oldClub.getPhoneNumber());
        oldClub.setLocation(oldClub.getLocation());
        oldClub.setCategory(category);
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
