package ruslan.shastkiv.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ruslan.shastkiv.bookstore.dto.book.BookDtoWithoutCategoryIds;
import ruslan.shastkiv.bookstore.dto.category.CategoryDto;
import ruslan.shastkiv.bookstore.dto.category.CategoryRequestDto;
import ruslan.shastkiv.bookstore.service.book.BookService;
import ruslan.shastkiv.bookstore.service.category.CategoryService;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Operations related to categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new category",
            description = "This endpoint allows an admin to add a new category.")
    public CategoryDto addCategory(@RequestBody @Valid CategoryRequestDto requestDto) {
        return categoryService.addCategory(requestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all categories",
            description = "This endpoint allows users to fetch a list of all categories.")
    public Page<CategoryDto> getAllCategories(@ParameterObject @PageableDefault Pageable pageable) {
        return categoryService.getAllCategories(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get category by ID",
            description = "This endpoint allows users to fetch a category by its ID.")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/{id}/books")
    @Operation(summary = "Get all books by category ID",
            description = "This endpoint allows users to fetch a list of books "
                    + "for a specific category.")
    public Page<BookDtoWithoutCategoryIds> getAllBooksByCategoryId(
            @PathVariable Long id,
            @ParameterObject @PageableDefault Pageable pageable) {
        return bookService.getAllBooksByCategoryId(id, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update category by ID",
            description = "This endpoint allows an admin to update an existing category by its ID.")
    public CategoryDto updateCategoryById(
            @PathVariable Long id, @RequestBody @Valid CategoryRequestDto requestDto) {
        return categoryService.updateCategory(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete category by ID",
            description = "This endpoint allows an admin to delete a category by its ID.")
    public void deleteCategoryById(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
