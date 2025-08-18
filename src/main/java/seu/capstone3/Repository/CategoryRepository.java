package seu.capstone3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import seu.capstone3.Model.Category;

@Repository
public interface CategoryRepository  extends JpaRepository<Category, Integer> {

    Category findCategoryById(Integer id);

}
