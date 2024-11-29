package ruslan.shastkiv.bookstore.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.test.web.servlet.MvcResult;
import ruslan.shastkiv.bookstore.dto.category.CategoryDto;
import ruslan.shastkiv.bookstore.dto.category.CategoryRequestDto;
import ruslan.shastkiv.bookstore.model.Category;

public class CategoryTestUtils {
    public static final Long FIRST_CATEGORY_ID = 1L;
    public static final Long SECOND_CATEGORY_ID = 2L;
    public static final Long THIRD_CATEGORY_ID = 3L;
    public static final Long NON_EXISTED_CATEGORY_ID = 999L;

    public static final String CUSTOM_CATEGORY_NAME = "Category_%s";
    public static final String CUSTOM_CATEGORY_DESCRIPTION = "Description_%s";

    public static final String UPDATED_CATEGORY_NAME = "Category_%s_updated";

    public static final String CATEGORY_URL = "/categories";
    public static final String CATEGORY_URL_WITH_FIRST_ID = "/categories/1";
    public static final String CATEGORY_URL_WITH_SECOND_ID = "/categories/2/books";

    public static final int ONE_INVOCATION = 1;

    public static CategoryRequestDto createCategoryRequestDto(Long id) {
        return new CategoryRequestDto(
                CUSTOM_CATEGORY_NAME.formatted(id),
                CUSTOM_CATEGORY_DESCRIPTION.formatted(id));
    }

    public static CategoryRequestDto createUpdatedCategoryRequestDto(Long id) {
        return new CategoryRequestDto(
                UPDATED_CATEGORY_NAME.formatted(id),
                CUSTOM_CATEGORY_DESCRIPTION.formatted(id));
    }

    public static CategoryDto createUpdatedCategoryDto(Long id) {
        return new CategoryDto(id,
                UPDATED_CATEGORY_NAME.formatted(id),
                CUSTOM_CATEGORY_DESCRIPTION.formatted(id)
        );
    }

    public static CategoryDto createCategoryDtoById(Long id) {
        return new CategoryDto(
                id,
                CUSTOM_CATEGORY_NAME.formatted(id),
                CUSTOM_CATEGORY_DESCRIPTION.formatted(id)
        );
    }

    public static Category createCategoryById(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName(CUSTOM_CATEGORY_NAME.formatted(id));
        category.setDescription(CUSTOM_CATEGORY_DESCRIPTION.formatted(id));
        return category;
    }

    public static Category createUpdatedCategoryById(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName(UPDATED_CATEGORY_NAME.formatted(id));
        category.setDescription(CUSTOM_CATEGORY_DESCRIPTION.formatted(id));
        return category;
    }

    public static List<CategoryDto> getCategoryDtosFromMvcResult(
            MvcResult result,
            ObjectMapper objectMapper) throws Exception {
        return objectMapper.convertValue(
                objectMapper.readTree(result.getResponse().getContentAsString())
                        .get("content"), new TypeReference<List<CategoryDto>>() {}
        );
    }
}
