package ruslan.shastkiv.bookstore.service.category;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import ruslan.shastkiv.bookstore.dto.category.CategoryDto;
import ruslan.shastkiv.bookstore.dto.category.CategoryRequestDto;

import java.util.List;


public interface CategoryService {
    CategoryDto addCategory(@Valid CategoryRequestDto requestDto);

    List<CategoryDto> getAllCategories();

    CategoryDto getCategoryById(Long id);

    CategoryDto updateCategory(Long id, @Valid CategoryRequestDto requestDto);

    void deleteCategory(Long id);
}
