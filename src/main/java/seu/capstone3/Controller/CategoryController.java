package seu.capstone3.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seu.capstone3.Api.ApiResponse;
import seu.capstone3.Model.Category;
import seu.capstone3.Service.CategoryService;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;


    @GetMapping("/get")
    public ResponseEntity<?> getAllCategories(){
        return ResponseEntity.status(200).body(categoryService.getAllCategory());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@Valid @RequestBody Category category){
        categoryService.addCategory(category);
        return ResponseEntity.status(200).body(new ApiResponse("Category added successfully"));
    }

    @PutMapping("/update/{category_id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer category_id,@Valid @RequestBody Category category){
        categoryService.updateCategory(category_id,category);
        return ResponseEntity.status(200).body(new ApiResponse("Category updated successfully"));
    }

    @DeleteMapping("/delete/{category_id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer category_id){
        categoryService.deleteCategory(category_id);
        return ResponseEntity.status(200).body(new ApiResponse("Category deleted successfully"));
    }
}
