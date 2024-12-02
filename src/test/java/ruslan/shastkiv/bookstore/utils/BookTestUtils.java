package ruslan.shastkiv.bookstore.utils;

import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.FIRST_CATEGORY_ID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MvcResult;
import ruslan.shastkiv.bookstore.dto.book.BookDto;
import ruslan.shastkiv.bookstore.dto.book.BookDtoWithoutCategoryIds;
import ruslan.shastkiv.bookstore.dto.book.BookSearchParametersDto;
import ruslan.shastkiv.bookstore.dto.book.CreateBookRequestDto;
import ruslan.shastkiv.bookstore.model.Book;

public class BookTestUtils {
    public static final int PAGE_SIZE_1 = 1;
    public static final int PAGE_SIZE_2 = 2;
    public static final int EMPTY_PAGE = 0;
    public static final Pageable PAGEABLE = PageRequest.of(0, 10);

    public static final Long FIRST_BOOK_ID = 1L;
    public static final Long SECOND_BOOK_ID = 2L;
    public static final Long THIRD_BOOK_ID = 3L;
    public static final Long FOURTH_BOOK_ID = 4L;
    public static final Long INVALID_BOOK_ID = 100L;

    public static final String UPDATED_TITLE = "Updated_title_1";
    public static final String NON_EXISTING_TITLE = "Non_existing_title_1";

    public static final String CUSTOM_BOOK_TITLE = "Title_%s";
    public static final String CUSTOM_BOOK_AUTHOR = "Author_%s";
    public static final String CUSTOM_BOOK_DESCRIPTION = "Description_%s";
    public static final String CUSTOM_BOOK_ISBN = "978-3-16-148410-%s";
    public static final String CUSTOM_BOOK_COVER_IMAGE = "https://cover_image_%s.jpg";

    public static final String BOOK_URL = "/books";
    public static final String BOOK_URL_WITH_INVALID_ID = "/books/100";
    public static final String BOOK_URL_WITH_FIRST_BOOK_ID = "/books/1";
    public static final String BOOK_URL_WITH_SECOND_BOOK_ID = "/books/2";
    public static final String SEARCH_BOOK_URL = "/books/search";

    public static CreateBookRequestDto createBookRequestDtoById(Long id) {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle(CUSTOM_BOOK_TITLE.formatted(id));
        requestDto.setAuthor(CUSTOM_BOOK_AUTHOR.formatted(id));
        requestDto.setDescription(CUSTOM_BOOK_DESCRIPTION.formatted(id));
        requestDto.setIsbn(CUSTOM_BOOK_ISBN.formatted(id));
        requestDto.setPrice(BigDecimal.valueOf(id));
        requestDto.setCoverImage(CUSTOM_BOOK_COVER_IMAGE.formatted(id));
        requestDto.setCategoryIds(Set.of(FIRST_CATEGORY_ID));
        return requestDto;
    }

    public static CreateBookRequestDto updateBookRequestDtoById(Long id) {
        CreateBookRequestDto requestDto = createBookRequestDtoById(id);
        requestDto.setTitle(UPDATED_TITLE);
        return requestDto;
    }

    public static BookSearchParametersDto createSearchParamsDto() {
        return new BookSearchParametersDto(
                new String[]{CUSTOM_BOOK_AUTHOR.formatted(FIRST_BOOK_ID)},
                new String[]{CUSTOM_BOOK_TITLE.formatted(FIRST_BOOK_ID)},
                new String[]{},
                new String[]{}
                );
    }

    public static BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds(Long id) {
        return new BookDtoWithoutCategoryIds(id,
                CUSTOM_BOOK_TITLE.formatted(id),
                CUSTOM_BOOK_AUTHOR.formatted(id),
                CUSTOM_BOOK_ISBN.formatted(id),
                BigDecimal.valueOf(id),
                CUSTOM_BOOK_DESCRIPTION.formatted(id),
                CUSTOM_BOOK_COVER_IMAGE.formatted(id)
        );
    }

    public static Book createBookById(Long id, List<Long> categoryIds) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(CUSTOM_BOOK_TITLE.formatted(id));
        book.setAuthor(CUSTOM_BOOK_AUTHOR.formatted(id));
        book.setDescription(CUSTOM_BOOK_DESCRIPTION.formatted(id));
        book.setIsbn(CUSTOM_BOOK_ISBN.formatted(id));
        book.setPrice(BigDecimal.valueOf(id));
        book.setCoverImage(CUSTOM_BOOK_COVER_IMAGE.formatted(id));
        book.setCategories(
                categoryIds.stream()
                .map(CategoryTestUtils::createCategoryById)
                .collect(Collectors.toSet())
        );
        return book;
    }

    public static BookDto createBookDtoById(Long id, List<Long> categoryIds) {
        BookDto bookDto = new BookDto();
        bookDto.setId(id);
        bookDto.setTitle(CUSTOM_BOOK_TITLE.formatted(id));
        bookDto.setAuthor(CUSTOM_BOOK_AUTHOR.formatted(id));
        bookDto.setDescription(CUSTOM_BOOK_DESCRIPTION.formatted(id));
        bookDto.setIsbn(CUSTOM_BOOK_ISBN.formatted(id));
        bookDto.setPrice(BigDecimal.valueOf(id));
        bookDto.setCoverImage(CUSTOM_BOOK_COVER_IMAGE.formatted(id));
        bookDto.setCategories(
                categoryIds.stream()
                        .map(CategoryTestUtils::createCategoryDtoById)
                        .collect(Collectors.toSet())
        );
        return bookDto;
    }

    public static List<BookDto> getBookDtosFromMvcResult(
            MvcResult result,
            ObjectMapper objectMapper) throws Exception {
        return objectMapper.convertValue(
                objectMapper.readTree(result.getResponse().getContentAsString())
                        .get("content"), new TypeReference<List<BookDto>>() {}
        );
    }

    public static List<BookDtoWithoutCategoryIds> getBookDtosWithoutCategoriesFromMvcResult(
            MvcResult result,
            ObjectMapper objectMapper) throws Exception {
        return objectMapper.convertValue(
                objectMapper.readTree(result.getResponse().getContentAsString())
                        .get("content"), new TypeReference<List<BookDtoWithoutCategoryIds>>() {}
        );
    }
}
