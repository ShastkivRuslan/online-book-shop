package ruslan.shastkiv.bookstore.utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ruslan.shastkiv.bookstore.dto.book.BookDto;
import ruslan.shastkiv.bookstore.dto.book.BookDtoWithoutCategoryIds;
import ruslan.shastkiv.bookstore.dto.book.BookSearchParametersDto;
import ruslan.shastkiv.bookstore.dto.book.CreateBookRequestDto;
import ruslan.shastkiv.bookstore.dto.category.CategoryDto;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.model.Category;

public class TestUtils {
    public static final int PAGE_SIZE = 1;
    public static final int EMPTY_PAGE = 0;
    public static final Pageable PAGEABLE = PageRequest.of(0, 10);

    public static final Long FIRST_BOOK_ID = 1L;
    public static final Long FOURTH_BOOK_ID = 4L;
    public static final Long INVALID_BOOK_ID = 100L;

    public static final BigDecimal BOOK_PRICE = BigDecimal.TEN;
    public static final String BOOK_TITLE = "Title_1";
    public static final String BOOK_AUTHOR = "Author_1";
    public static final String BOOK_DESCRIPTION = "Description_1";
    public static final String BOOK_ISBN = "978-3-16-148410-0";
    public static final String BOOK_COVER_IMAGE = "https://cover_image_1.jpg";
    public static final String UPDATED_TITLE = "Updated_title_1";
    public static final String NON_EXISTING_TITLE = "Non_existing_title_1";

    public static final String CUSTOM_BOOK_TITLE = "Title_%s";
    public static final String CUSTOM_BOOK_AUTHOR = "Author_%s";
    public static final String CUSTOM_BOOK_DESCRIPTION = "Description_%s";
    public static final String CUSTOM_BOOK_ISBN = "978-3-16-148410-%s";
    public static final String CUSTOM_BOOK_COVER_IMAGE = "https://cover_image_%s.jpg";

    public static final Long FIRST_CATEGORY_ID = 1L;
    public static final String CATEGORY_NAME = "Category_1";
    public static final String CATEGORY_DESCRIPTION = "Description_1";
    public static final String CUSTOM_CATEGORY_NAME = "Category_%s";
    public static final String CUSTOM_CATEGORY_DESCRIPTION = "Description_%s";

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

    public static Category createCategory() {
        Category category = new Category();
        category.setId(FIRST_CATEGORY_ID);
        category.setName(CATEGORY_NAME);
        category.setDescription(CATEGORY_DESCRIPTION);
        return category;
    }

    public static BookSearchParametersDto createSearchParamsDto() {
        return new BookSearchParametersDto(
                new String[]{BOOK_AUTHOR},
                new String[]{BOOK_TITLE},
                null,
                null
                );
    }

    public static BookDtoWithoutCategoryIds createBookDtoWithoutCategoryIds() {
        return new BookDtoWithoutCategoryIds(FIRST_BOOK_ID,
                BOOK_TITLE,
                BOOK_AUTHOR,
                BOOK_ISBN,
                BOOK_PRICE,
                BOOK_DESCRIPTION,
                BOOK_COVER_IMAGE
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
                .map(TestUtils::createCategoryById)
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
                        .map(TestUtils::createCategoryDtoById)
                        .collect(Collectors.toSet())
        );
        return bookDto;
    }

    public static Category createCategoryById(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName(CUSTOM_CATEGORY_NAME.formatted(id));
        category.setDescription(CUSTOM_CATEGORY_DESCRIPTION.formatted(id));
        return category;
    }

    public static CategoryDto createCategoryDtoById(Long id) {
        return new CategoryDto(
                id,
                CUSTOM_CATEGORY_NAME.formatted(id),
                CUSTOM_CATEGORY_DESCRIPTION.formatted(id)
        );
    }
}
