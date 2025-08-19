package seu.capstone3.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import seu.capstone3.Api.ApiException;
import seu.capstone3.Model.Category;
import seu.capstone3.Repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }


    public void addCategory(Category category) {

        Category existingCategory = categoryRepository.findCategoriesByName(category.getName());
        if (existingCategory != null) {
            throw new ApiException("Category already exist");
        }
        categoryRepository.save(category);
    }


    public void updateCategory(Integer id , Category category) {
        Category oldCategory = categoryRepository.findCategoryById(id);
        if (oldCategory == null) {
            throw new ApiException("Category not found");
        }
        oldCategory.setName(category.getName());
        categoryRepository.save(oldCategory);
    }


    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findCategoryById(id);
        if (category == null) {
            throw new ApiException("Category not found");
        }
        categoryRepository.delete(category);
    }


}
