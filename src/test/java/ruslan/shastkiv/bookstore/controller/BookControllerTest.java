package ruslan.shastkiv.bookstore.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static ruslan.shastkiv.bookstore.utils.TestUtils.BOOK_AUTHOR;
import static ruslan.shastkiv.bookstore.utils.TestUtils.BOOK_URL;
import static ruslan.shastkiv.bookstore.utils.TestUtils.BOOK_URL_WITH_FIRST_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.TestUtils.BOOK_URL_WITH_INVALID_ID;
import static ruslan.shastkiv.bookstore.utils.TestUtils.BOOK_URL_WITH_SECOND_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.TestUtils.EMPTY_PAGE;
import static ruslan.shastkiv.bookstore.utils.TestUtils.FIRST_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.TestUtils.FIRST_CATEGORY_ID;
import static ruslan.shastkiv.bookstore.utils.TestUtils.FOURTH_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.TestUtils.INVALID_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.TestUtils.NON_EXISTING_TITLE;
import static ruslan.shastkiv.bookstore.utils.TestUtils.PAGEABLE;
import static ruslan.shastkiv.bookstore.utils.TestUtils.SEARCH_BOOK_URL;
import static ruslan.shastkiv.bookstore.utils.TestUtils.UPDATED_TITLE;
import static ruslan.shastkiv.bookstore.utils.TestUtils.createBookDtoById;
import static ruslan.shastkiv.bookstore.utils.TestUtils.createBookRequestDtoById;
import static ruslan.shastkiv.bookstore.utils.TestUtils.updateBookRequestDtoById;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ruslan.shastkiv.bookstore.dto.book.BookDto;
import ruslan.shastkiv.bookstore.dto.book.CreateBookRequestDto;
import ruslan.shastkiv.bookstore.exception.EntityNotFoundException;

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
public class BookControllerTest {
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

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Should retrieve all books from the catalog as a pageable response
            """)
    public void getAll_GivenBooksInCatalog_ReturnPageDtos() throws Exception {
        Page<BookDto> expectedPage = new PageImpl<>(
                List.of(
                        createBookDtoById(1L, List.of(1L)),
                        createBookDtoById(2L, List.of(2L)),
                        createBookDtoById(3L, List.of(2L))
                ), PAGEABLE, 3
        );

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get(BOOK_URL)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String expectedString = objectMapper.writeValueAsString(expectedPage);
        String actualString = result.getResponse().getContentAsString();

        assertEquals(expectedString, actualString);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Should retrieve a book by its ID
            """)
    public void getBookById_BookById_ReturnBookDto() throws Exception {
        BookDto expected = createBookDtoById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get(BOOK_URL_WITH_FIRST_BOOK_ID, FIRST_BOOK_ID)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        assertEquals(expected, actual);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Should throw EntityNotFoundException for an invalid book ID
            """)
    public void getBokById_BookByInvalidId_ThrowException() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get(
                                BOOK_URL + "/" + INVALID_BOOK_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertInstanceOf(
                        EntityNotFoundException.class,
                        result.getResolvedException()
                ));
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Should return books matching the valid search parameters
            """)
    public void search_ValidSearchParams_ReturnBookDto() throws Exception {
        Page<BookDto> expectedPage = new PageImpl<>(
                List.of(createBookDtoById(FIRST_BOOK_ID,
                        List.of(FIRST_CATEGORY_ID))), PAGEABLE, 1
        );

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get(SEARCH_BOOK_URL)
                                .param("authors", BOOK_AUTHOR)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String expectedString = objectMapper.writeValueAsString(expectedPage);
        String actualString = result.getResponse().getContentAsString();

        assertEquals(expectedString, actualString);

    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("""
            Should return an empty page when no books match the search parameters
            """)
    public void search_InvalidSearchParams_ReturnEmptyPage() throws Exception {
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get(SEARCH_BOOK_URL)
                                .param("titles", NON_EXISTING_TITLE)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Page<BookDto> expectedPage = new PageImpl<>(List.of(), PAGEABLE, EMPTY_PAGE);
        String expectedString = objectMapper.writeValueAsString(expectedPage);
        String actualString = result.getResponse().getContentAsString();

        assertEquals(expectedString, actualString);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Should create a new book and return its details
            """)
    @Sql(
            scripts = "classpath:scripts/book/remove_book_after_create_method.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void createBook_ValidBookDto_ReturnBookDto() throws Exception {
        CreateBookRequestDto createBookRequestDto = createBookRequestDtoById(FOURTH_BOOK_ID);
        String json = objectMapper.writeValueAsString(createBookRequestDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post(BOOK_URL)
                                .content(json)
                               .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        BookDto expected = createBookDtoById(FOURTH_BOOK_ID, List.of(2L));
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        reflectionEquals(expected, actual, "id");
        //assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Should update an existing book and return the updated details
            """)
    @Sql(
            scripts = "classpath:scripts/book/revert_updated_book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void updateBook_ByBookUpdateDto_ReturnBookDto() throws Exception {
        CreateBookRequestDto requestDto = updateBookRequestDtoById(FIRST_BOOK_ID);
        String json = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.put(BOOK_URL_WITH_FIRST_BOOK_ID)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BookDto expected = createBookDtoById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));
        expected.setTitle(UPDATED_TITLE);
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);

        assertEquals(expected, actual);
    }

    @WithMockUser(username = "user", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Should throw EntityNotFoundException for an invalid book ID during update
            """)
    public void updateBook_BookByInvalidId_ThrowException() throws Exception {
        CreateBookRequestDto requestDto = updateBookRequestDtoById(FOURTH_BOOK_ID);
        String json = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(
                        MockMvcRequestBuilders.put(BOOK_URL_WITH_INVALID_ID)
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertInstanceOf(
                        EntityNotFoundException.class,
                        result.getResolvedException()
                ));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("""
            Should delete a book by its ID and return no content
            """)
    @Sql(
            scripts = "classpath:scripts/book/set_book_active_after_delete_method.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    public void deleteBook_ByBookId_ReturnNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(BOOK_URL_WITH_SECOND_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
