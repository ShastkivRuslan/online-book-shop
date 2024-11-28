package ruslan.shastkiv.bookstore.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.PAGEABLE;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.PAGE_SIZE_2;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.FIRST_CATEGORY_ID;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.NON_EXISTED_CATEGORY_ID;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.ONE_INVOCATION;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.SECOND_CATEGORY_ID;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.createCategoryById;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.createCategoryDtoById;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.createCategoryRequestDto;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.createUpdatedCategoryById;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.createUpdatedCategoryDto;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.createUpdatedCategoryRequestDto;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ruslan.shastkiv.bookstore.dto.category.CategoryDto;
import ruslan.shastkiv.bookstore.dto.category.CategoryRequestDto;
import ruslan.shastkiv.bookstore.exception.EntityNotFoundException;
import ruslan.shastkiv.bookstore.mapper.CategoryMapper;
import ruslan.shastkiv.bookstore.model.Category;
import ruslan.shastkiv.bookstore.repository.category.CategoryRepository;
import ruslan.shastkiv.bookstore.service.category.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Should add a new category and return its DTO")
    public void addCategory_GivenValidRequestDto_ReturnCategoryDto() {
        CategoryRequestDto requestDto = createCategoryRequestDto(FIRST_CATEGORY_ID);
        Category category = createCategoryById(FIRST_CATEGORY_ID);
        CategoryDto expected = createCategoryDtoById(FIRST_CATEGORY_ID);

        when(categoryMapper.toModel(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.addCategory(requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should retrieve all categories as a pageable response")
    public void getAllCategories_WhenCategoriesExist_ReturnPageOfCategoryDtos() {
        Category firstCategory = createCategoryById(FIRST_CATEGORY_ID);
        Category secondCategory = createCategoryById(SECOND_CATEGORY_ID);

        CategoryDto firstCategoryDto = createCategoryDtoById(FIRST_CATEGORY_ID);
        CategoryDto secondCategoryDto = createCategoryDtoById(SECOND_CATEGORY_ID);

        Page<Category> categoryPage = new PageImpl<>(
                List.of(firstCategory,
                        secondCategory
                ), PAGEABLE, PAGE_SIZE_2
        );

        when(categoryRepository.findAll(PAGEABLE)).thenReturn(categoryPage);
        when(categoryMapper.toDto(firstCategory)).thenReturn(firstCategoryDto);
        when(categoryMapper.toDto(secondCategory)).thenReturn(secondCategoryDto);

        Page<CategoryDto> actual = categoryService.getAllCategories(PAGEABLE);

        assertEquals(PAGE_SIZE_2, actual.getTotalElements());
        assertEquals(PAGEABLE.getPageSize(), actual.getSize());
        assertEquals(List.of(firstCategoryDto, secondCategoryDto), actual.getContent());

        // Verify interactions
        verify(categoryRepository, times(ONE_INVOCATION)).findAll(PAGEABLE);
        verify(categoryMapper, times(ONE_INVOCATION)).toDto(firstCategory);
        verify(categoryMapper, times(ONE_INVOCATION)).toDto(secondCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Should retrieve a category by its ID and return its DTO")
    public void getCategoryById_GivenValidId_ReturnCategoryDto() {
        Category category = createCategoryById(FIRST_CATEGORY_ID);
        CategoryDto expected = createCategoryDtoById(FIRST_CATEGORY_ID);

        when(categoryRepository.findById(FIRST_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.getCategoryById(FIRST_CATEGORY_ID);

        assertEquals(expected, actual);
        verify(categoryRepository, times(ONE_INVOCATION)).findById(FIRST_CATEGORY_ID);
        verify(categoryMapper, times(ONE_INVOCATION)).toDto(category);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException for an invalid category ID")
    public void getCategoryById_GivenInvalidId_ThrowEntityNotFoundException() {
        when(categoryRepository.findById(NON_EXISTED_CATEGORY_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.getCategoryById(NON_EXISTED_CATEGORY_ID));

        verifyNoInteractions(categoryMapper);
    }

    @Test
    @DisplayName("Should update an existing category and return updated DTO")
    public void updateCategory_GivenValidIdAndRequestDto_ReturnUpdatedCategoryDto() {
        Category category = createCategoryById(FIRST_CATEGORY_ID);
        CategoryRequestDto requestDto = createCategoryRequestDto(FIRST_CATEGORY_ID);

        Category updatedCategory = createUpdatedCategoryById(FIRST_CATEGORY_ID);
        CategoryDto expected = createUpdatedCategoryDto(FIRST_CATEGORY_ID);

        when(categoryRepository.findById(FIRST_CATEGORY_ID)).thenReturn(Optional.of(category));
        doNothing().when(categoryMapper).updateCategoryFromDto(requestDto, category);
        when(categoryRepository.save(category)).thenReturn(updatedCategory);
        when(categoryMapper.toDto(updatedCategory)).thenReturn(expected);

        CategoryDto actual = categoryService.updateCategory(FIRST_CATEGORY_ID, requestDto);

        assertEquals(expected, actual);
        verify(categoryRepository, times(ONE_INVOCATION)).findById(FIRST_CATEGORY_ID);
        verify(categoryMapper, times(ONE_INVOCATION)).updateCategoryFromDto(requestDto, category);
        verify(categoryRepository, times(ONE_INVOCATION)).save(category);
        verify(categoryMapper, times(ONE_INVOCATION)).toDto(updatedCategory);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent category")
    public void updateCategory_GivenInvalidId_ThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class,
                () -> categoryService.updateCategory(
                        NON_EXISTED_CATEGORY_ID,
                        createUpdatedCategoryRequestDto(NON_EXISTED_CATEGORY_ID)
                )
        );
    }

    @Test
    @DisplayName("Should delete a category by its ID")
    public void deleteCategory_GivenValidId_DeleteCategory() {
        categoryService.deleteCategory(FIRST_CATEGORY_ID);

        verify(categoryRepository).deleteById(FIRST_CATEGORY_ID);
        verifyNoMoreInteractions(categoryRepository);
    }

}
