package ruslan.shastkiv.bookstore.service.category;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ruslan.shastkiv.bookstore.dto.category.CategoryDto;
import ruslan.shastkiv.bookstore.dto.category.CategoryRequestDto;

public interface CategoryService {
    CategoryDto addCategory(@Valid CategoryRequestDto requestDto);

    Page<CategoryDto> getAllCategories(Pageable pageable);

    CategoryDto getCategoryById(Long id);

    CategoryDto updateCategory(Long id, @Valid CategoryRequestDto requestDto);

    void deleteCategory(Long id);
}
