package ruslan.shastkiv.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.CUSTOM_BOOK_TITLE;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.PAGEABLE;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.PAGE_SIZE_2;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.SECOND_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.THIRD_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.NON_EXISTED_CATEGORY_ID;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.SECOND_CATEGORY_ID;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.repository.book.BookRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
            findBooksByCategoryId()
            - should return a page with books for an existing category ID
            """)
    void findBooksByCategoryId_ExistingCategoryId_ReturnsPageWithBooks() {
        List<String> expectedTitles = List.of(
                CUSTOM_BOOK_TITLE.formatted(SECOND_BOOK_ID),
                CUSTOM_BOOK_TITLE.formatted(THIRD_BOOK_ID));

        List<String> actualTitles
                = bookRepository.findBooksByCategoryId(SECOND_CATEGORY_ID, PAGEABLE).stream()
                .map(Book::getTitle)
                .toList();

        assertEquals(expectedTitles, actualTitles);
    }

    @Test
    @DisplayName("""
            findBooksByCategoryId()
            - should return an empty page when the category ID does not exist
            """)
    void findBooksByCategoryId_NonExistingCategoryId_ReturnsEmptyPage() {
        Page<Book> actual = bookRepository.findBooksByCategoryId(NON_EXISTED_CATEGORY_ID, PAGEABLE);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("""
            findBooksByCategoryId()
            - should return 2 pages containing books, pagination test
            """)
    void findBooksByCategoryId_paginationTest_returnsTwoPagesWithBooks() {
        Pageable pageable = PageRequest.of(2, 1);

        int actualPagesQuantity = bookRepository.findBooksByCategoryId(
                SECOND_CATEGORY_ID, pageable).getTotalPages();

        assertEquals(PAGE_SIZE_2, actualPagesQuantity);
    }

    @Test
    @DisplayName("""
            findBooksByCategoryId()
            - should return a page with books which is not deleted for an existing category ID
            """)
    @Sql(scripts =
            "classpath:scripts/book/soft_delete_one_book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findBooksByCategoryId_softDeleteTest_returnsOnlyNonDeletedBooks() {
        List<String> expectedTitles = List.of(
                CUSTOM_BOOK_TITLE.formatted(SECOND_BOOK_ID)
        );

        List<String> actualTitles
                = bookRepository.findBooksByCategoryId(SECOND_CATEGORY_ID, PAGEABLE).stream()
                .map(Book::getTitle)
                .toList();

        assertEquals(expectedTitles, actualTitles);
    }

    @Test
    @DisplayName("""
            findAllWithCategories()
            - should fetch books with initialized categories without LazyInitializationException
            """)
    void findAllWithCategories_getInitializedCategory_DoesNotThrowLazyInitializationException() {
        List<Book> allWithCategories = bookRepository.findAllWithCategories(PAGEABLE).getContent();

        assertAll(
                allWithCategories.stream()
                        .map(book -> () -> assertDoesNotThrow(
                                () -> book.getCategories().size(),
                                "LazyInitializationException was thrown for book: "
                                        + book.getTitle()
                        ))
        );
    }
}
