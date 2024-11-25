package ruslan.shastkiv.bookstore.repository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ruslan.shastkiv.bookstore.utils.TestUtils.PAGEABLE;

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
            Should return a page with books for an existing category ID
            """)
    void findBooksByCategoryId_ExistingCategoryId_ReturnsPageWithBooks() {
        List<String> expectedTitles = List.of("Title_2", "Title_3");
        Long categoryId = 2L;

        List<String> actualTitles
                = bookRepository.findBooksByCategoryId(categoryId, PAGEABLE).stream()
                .map(Book::getTitle)
                .toList();

        assertEquals(expectedTitles, actualTitles);
    }

    @Test
    @DisplayName("""
            Should return an empty page when the category ID does not exist
            """)
    void findBooksByCategoryId_NonExistingCategoryId_ReturnsEmptyPage() {
        Long nonExistedId = 10L;

        Page<Book> actual = bookRepository.findBooksByCategoryId(nonExistedId, PAGEABLE);

        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("""
            Should return 2 pages containing books, pagination test
            """)
    void findBooksByCategoryId_paginationTest_returnsTwoPagesWithBooks() {
        Pageable pageable = PageRequest.of(2, 1);
        int expectedPagesQuantity = 2;
        Long categoryId = 2L;

        int actualPagesQuantity
                = bookRepository.findBooksByCategoryId(categoryId, pageable).getTotalPages();

        assertEquals(expectedPagesQuantity, actualPagesQuantity);
    }

    @Test
    @DisplayName("""
            Should return a page with books which is not deleted for an existing category ID
            """)
    @Sql(scripts =
            "classpath:scripts/book/soft_delete_one_book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findBooksByCategoryId_softDeleteTest_returnsOnlyNonDeletedBooks() {
        List<String> expectedTitles = List.of("Title_2");
        Long categoryId = 2L;

        List<String> actualTitles
                = bookRepository.findBooksByCategoryId(categoryId, PAGEABLE).stream()
                .map(Book::getTitle)
                .toList();

        assertEquals(expectedTitles, actualTitles);
    }

    @Test
    @DisplayName("""
            Should fetch books with initialized categories
            """)
    void findAllWithCategories_getInitializedCategory_DoesNotThrowLazyInitializationException() {
        List<Book> allWithCategories = bookRepository.findAllWithCategories(PAGEABLE).getContent();

        for (Book book : allWithCategories) {
            assertDoesNotThrow(() -> {
                book.getCategories().size();
            }, "LazyInitializationException was thrown for book: " + book.getTitle());
        }
    }
}
