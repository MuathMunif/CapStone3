package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.DTOIN.PlayerDTO;
import seu.capstone3.Model.Category;
import seu.capstone3.Model.Player;
import seu.capstone3.Repository.CategoryRepository;
import seu.capstone3.Repository.PlayerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final CategoryRepository categoryRepository;

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
        playerRepository.save(player1);
    }


    public void deletePlayer(Integer id) {
        Player player = playerRepository.findPlayerById(id);
        if (player == null) {
            throw new ApiException("Player not found");
        }
        playerRepository.delete(player);
    }
}
