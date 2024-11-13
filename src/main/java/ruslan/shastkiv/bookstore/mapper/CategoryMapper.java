package ruslan.shastkiv.bookstore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ruslan.shastkiv.bookstore.config.MapperConfig;
import ruslan.shastkiv.bookstore.dto.category.CategoryDto;
import ruslan.shastkiv.bookstore.dto.category.CategoryRequestDto;
import ruslan.shastkiv.bookstore.model.Category;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toModel(CategoryRequestDto categoryDto);

    void updateCategoryFromDto(CategoryRequestDto requestDto, @MappingTarget Category category);
}
