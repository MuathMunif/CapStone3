package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import seu.capstone3.Api.ApiException;
import seu.capstone3.DTOIN.PlayerDTO;
import seu.capstone3.DTOOUT.PlayerOUTDTO;
import seu.capstone3.DTOOUT.PlayerSWAnalysisDTO;
import seu.capstone3.DTOOUT.TrainingPlanSimpleDTO;
import seu.capstone3.Model.Category;
import seu.capstone3.Model.Player;
import seu.capstone3.Repository.CategoryRepository;
import seu.capstone3.Repository.PlayerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final CategoryRepository categoryRepository;
    private final MinioService minioService;
    private final AiScoutingService aiScoutingService;

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }


    public void addPlayer(PlayerDTO playerDTO) {
        Category category = categoryRepository.findCategoryById(playerDTO.getCategory_id());
        if(category == null) {
            throw new ApiException("Category not found");
        }

        Player existingPlayer = playerRepository.findPlayerByEmail(playerDTO.getEmail());
        if (existingPlayer != null) {
            throw new ApiException("Player with this email: "+playerDTO.getEmail()+" already exists");
        }

        Player player = new Player(null,
                playerDTO.getName(),
                playerDTO.getEmail(),
                playerDTO.getPhoneNumber(),
                playerDTO.getAge(),
                playerDTO.getLocation(),
                playerDTO.getHeight(),
                playerDTO.getWeight(),
                playerDTO.getDescription(),
                playerDTO.getSkills(),
                null,
                null,
                null,
                category,
                null,
                null);

        playerRepository.save(player);
    }

    public void updatePlayer(Integer id ,Player player) {
        Player player1 = playerRepository.findPlayerById(id);
        if (player1 == null) {
            throw new ApiException("Player not found");
        }
        player1.setName(player.getName());
        player1.setAge(player.getAge());
        player1.setEmail(player.getEmail());
        player1.setPhoneNumber(player.getPhoneNumber());
        player1.setLocation(player.getLocation());
        player1.setHeight(player.getHeight());
        player1.setWeight(player.getWeight());
        player1.setDescription(player.getDescription());
        player1.setSkills(player.getSkills());
        player1.setCategory(player.getCategory());
        playerRepository.save(player1);
    }


    public void deletePlayer(Integer id) {
        Player player = playerRepository.findPlayerById(id);
        if (player == null) {
            throw new ApiException("Player not found");
        }

        player.getTournaments().forEach(t -> t.getPlayers().remove(player));
        player.getTournaments().clear();
        playerRepository.delete(player);
    }



    // upload player CV
    public void uploadCv(Integer playerId, MultipartFile cvFile) throws Exception {
        Player player = playerRepository.findPlayerById(playerId);
        if (player == null) {
            throw new ApiException("Player not found");
        }

        String objectName = "player-" + playerId + "-cv-" + cvFile.getOriginalFilename();

        String fileUrl = minioService.uploadFile(cvFile, objectName);

        player.setCvUrl(fileUrl);
        playerRepository.save(player);
    }


    // this to convert player to dto
    public PlayerOUTDTO convertToDTO(Player player) {
        PlayerOUTDTO dto = new PlayerOUTDTO();
        dto.setName(player.getName());
        dto.setEmail(player.getEmail());
        dto.setPhoneNumber(player.getPhoneNumber());
        dto.setAge(player.getAge());
        dto.setLocation(player.getLocation());
        dto.setHeight(player.getHeight());
        dto.setWeight(player.getWeight());
        dto.setDescription(player.getDescription());
        dto.setSkills(player.getSkills());
        dto.setCvUrl(player.getCvUrl());

        dto.setClubName(player.getClub() != null ? player.getClub().getName() : null);
        dto.setCategoryName(player.getCategory() != null ? player.getCategory().getName() : null);

        return dto;
    }


    // get player by ID with CV
    public PlayerOUTDTO getPlayerWithCv(Integer id) throws Exception {
        Player player = playerRepository.findPlayerById(id);
        if (player == null) {
            throw new ApiException("Player not found");
        }
        PlayerOUTDTO dto = convertToDTO(player);

        if (player.getCvUrl() != null) {
            String presignedUrl = minioService.getPresignedUrl(player.getCvUrl());
            dto.setCvUrl(presignedUrl);
        }

        return dto;
    }


    // get all players with dto
    public List<PlayerOUTDTO> getAllPlayersDto() {
        return playerRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    //get all players without club
    public List<PlayerOUTDTO> getPlayersWithoutClub() {
        return playerRepository.getPlayersWithoutClub()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Player getPlayerById(Integer id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));
    }

    public PlayerSWAnalysisDTO analyzePlayerStrengthsWeaknesses(Integer player_id) {
        Player player = getPlayerById(player_id);
        return aiScoutingService.analyzePlayerStrengthsWeaknesses(player);
    }

    public TrainingPlanSimpleDTO getTrainingPlanSimpleDto(Integer player_id,Integer days) {
        Player player = getPlayerById(player_id);
        return aiScoutingService.generateAutoTrainingPlan(player,days);
    }
}
