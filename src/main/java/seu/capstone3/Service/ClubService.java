package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.DTOIN.ClubDTO;
import seu.capstone3.DTOOUT.ClubOUTDTO;
import seu.capstone3.Model.Category;
import seu.capstone3.Model.Club;
import seu.capstone3.Model.Player;
import seu.capstone3.Model.RecruitmentOpportunity;
import seu.capstone3.Repository.CategoryRepository;
import seu.capstone3.Repository.ClubRepository;
import seu.capstone3.Repository.PlayerRepository;
import seu.capstone3.Repository.RecruitmentOpportunityRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final CategoryRepository categoryRepository;
    private final PlayerRepository playerRepository;
    private final RecruitmentOpportunityRepository recruitmentOpportunityRepository;
    private final EmailService emailService;


    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }


    public void addClub(ClubDTO clubDTO) {
        Category category = categoryRepository.findCategoryById(clubDTO.getCategory_id());
        if (category == null) {
            throw new ApiException("Category not found");
        }
        Club existingClub = clubRepository.findClubByEmail(clubDTO.getEmail());
        if (existingClub != null) {
            throw new ApiException("Club with this email: " + clubDTO.getEmail() + " already exist");
        }
        Club club = new Club(null, clubDTO.getCr(), clubDTO.getName(), clubDTO.getEmail(), clubDTO.getPhoneNumber(), clubDTO.getLocation(), null, null, category);
        clubRepository.save(club);
    }

    public void updateClub(Integer id, Club club) {
        Club oldClub = clubRepository.findClubById(id);
        Category category = categoryRepository.findCategoryById(club.getCategory().getId());
        if (oldClub == null) {
            throw new ApiException("Club not found");
        }
        if (category == null) {
            throw new ApiException("Category not found");
        }
        oldClub.setName(club.getName());
        oldClub.setEmail(club.getEmail());
        oldClub.setPhoneNumber(club.getPhoneNumber());
        oldClub.setLocation(club.getLocation());
        oldClub.setCategory(category);
        clubRepository.save(oldClub);
    }

    public void deleteClub(Integer id) {
        Club club = clubRepository.findClubById(id);
        if (club == null) {
            throw new ApiException("Club not found");
        }
        clubRepository.delete(club);
    }

    //EX

    public Club getClubById(Integer id) {
        Club club = clubRepository.findClubById(id);
        if (club == null) {
            throw new ApiException("Club not found");
        }
        return club;
    }


    // this method to convert to dto
    public ClubOUTDTO convertToDTO(Club club) {
        ClubOUTDTO dto = new ClubOUTDTO();
        dto.setName(club.getName());
        dto.setEmail(club.getEmail());
        dto.setPhoneNumber(club.getPhoneNumber());
        dto.setLocation(club.getLocation());

        dto.setCategoryName(club.getCategory() != null ? club.getCategory().getName() : null);

        if (club.getPlayers() != null) {
            dto.setPlayerNames(
                    club.getPlayers().stream()
                            .map(Player::getName)
                            .toList()
            );
        }
        return dto;
    }

    // get club by Id dto
    public ClubOUTDTO getClubByIdDto(Integer id) {
        Club club = clubRepository.findClubById(id);
        if (club == null) {
            throw new ApiException("Club not found");
        }
        return convertToDTO(club);
    }


    // get all club dto
    public List<ClubOUTDTO> getAllClubsDto() {
        return clubRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // get all club by location 'DTO'
    public List<ClubOUTDTO> getClubsByLocation(String location) {
        return clubRepository.findByLocationIgnoreCase(location)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    public List<ClubOUTDTO> getClubsByCategory(Integer categoryId) {
        return clubRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public void sendQualifiedEmailToPlayer(Integer recruitmentOpportunity_id, Integer player_id,Integer club_id) {

        RecruitmentOpportunity recruitmentOpportunity = recruitmentOpportunityRepository.findRecruitmentOpportunitiesByIdAndClub_Id(recruitmentOpportunity_id, club_id);
        if (recruitmentOpportunity == null) {
            throw new ApiException("Recruitment opportunity not found for this club");
        }
        Player player = playerRepository.findPlayerById(player_id);
        if (player == null) {
            throw new ApiException("Player not found");
        }

        Club club = clubRepository.findClubById(club_id);
        if (club == null) {
            throw new ApiException("Club not found");
        }

        emailService.qualifiedEmail(recruitmentOpportunity, player,club);
    }
}
