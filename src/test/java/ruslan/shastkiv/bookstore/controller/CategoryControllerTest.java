package ruslan.shastkiv.bookstore.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.createBookDtoWithoutCategoryIds;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.getBookDtosWithoutCategoriesFromMvcResult;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.CATEGORY_URL;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.CATEGORY_URL_WITH_FIRST_ID;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.CATEGORY_URL_WITH_SECOND_ID;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.FIRST_CATEGORY_ID;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.SECOND_CATEGORY_ID;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.THIRD_CATEGORY_ID;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.createCategoryDtoById;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.createCategoryRequestDto;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.createUpdatedCategoryDto;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.createUpdatedCategoryRequestDto;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.getCategoryDtosFromMvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ruslan.shastkiv.bookstore.dto.book.BookDtoWithoutCategoryIds;
import ruslan.shastkiv.bookstore.dto.category.CategoryDto;
import ruslan.shastkiv.bookstore.dto.category.CategoryRequestDto;

@Sql(scripts = {
        "classpath:scripts/book/insert_books_to_db.sql",
        "classpath:scripts/category/insert_categories_to_db.sql",
        "classpath:scripts/book/insert_relations_between_book_and_categories.sql",
},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {
        "classpath:scripts/book/remove_relations_between_books_and_categories_from_db.sql",
        "classpath:scripts/category/remove_categories_from_db.sql",
        "classpath:scripts/book/remove_test_books_from_db.sql"
},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("""
            Should add a category successfully and return category DTO
            """)
    @Sql(
            scripts = "classpath:scripts/category/delete_category_after_create.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void addCategory_ValidInput_ReturnCategoryDto() throws Exception {
        CategoryRequestDto requestDto = createCategoryRequestDto(THIRD_CATEGORY_ID);
        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post(CATEGORY_URL)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        CategoryDto expected = createCategoryDtoById(THIRD_CATEGORY_ID);
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            Should fetch all categories as a pageable response
            """)
    public void getAllCategories_AsUser_ReturnPageOfCategories() throws Exception {
        List<CategoryDto> expectedDtos = List.of(
                createCategoryDtoById(FIRST_CATEGORY_ID),
                createCategoryDtoById(SECOND_CATEGORY_ID)
        );

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get(CATEGORY_URL)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List<CategoryDto> actualDtos = getCategoryDtosFromMvcResult(result, objectMapper);

        assertTrue(expectedDtos.stream()
                .allMatch(expectedDto -> actualDtos.stream()
                        .anyMatch(actualDto -> actualDto.equals(expectedDto))));
    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            Should fetch category details by ID as a user
            """)
    public void getCategoryById_ValidId_ReturnCategoryDetails() throws Exception {
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get(CATEGORY_URL_WITH_FIRST_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        CategoryDto expected = createCategoryDtoById(FIRST_CATEGORY_ID);
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertEquals(expected, actual);

    }

    @Test
    @WithMockUser(username = "user")
    @DisplayName("""
            Should fetch all books by category ID as a pageable response
            """)
    public void getAllBooksByCategoryId_ValidCategoryId_ReturnPageOfBooks() throws Exception {
        List<BookDtoWithoutCategoryIds> expectedDtos = List.of(
                createBookDtoWithoutCategoryIds(2L),
                createBookDtoWithoutCategoryIds(3L)
        );

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get(CATEGORY_URL_WITH_SECOND_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        List<BookDtoWithoutCategoryIds> actualDtos
                = getBookDtosWithoutCategoriesFromMvcResult(result, objectMapper);

        assertTrue(expectedDtos.stream()
                .allMatch(expectedDto -> actualDtos.stream()
                        .anyMatch(actualDto -> actualDto.equals(expectedDto))));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("""
            Should update category details by ID as an admin
            """)
    @Sql(
            scripts = "classpath:scripts/category/revert_updated_category.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void updateCategoryById_ValidIdAsAdmin_ReturnUpdatedCategory() throws Exception {
        CategoryRequestDto requestDto = createUpdatedCategoryRequestDto(FIRST_CATEGORY_ID);
        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.put(CATEGORY_URL_WITH_FIRST_ID)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        CategoryDto expected = createUpdatedCategoryDto(FIRST_CATEGORY_ID);
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("""
            Should delete a category by ID as an admin
            """)
    @Sql(
            scripts = "classpath:scripts/category/set_category_active_after_soft_delete.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void deleteCategoryById_ValidIdAsAdmin_NoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(CATEGORY_URL_WITH_FIRST_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
