package ruslan.shastkiv.bookstore.utils;

import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.model.Category;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class TestUtils {
    protected Set<Book> initTestBooks() {
        Book firstBook = new Book();
        firstBook.setId(1L);
        firstBook.setTitle("Name_of_book_1");
        firstBook.setAuthor("Author_1");
        firstBook.setIsbn("978-3-16-148410-0");
        firstBook.setPrice(BigDecimal.valueOf(5));
        firstBook.setDescription("Description_of_book_1");
        firstBook.setCoverImage("cover_1.jpg");
        firstBook.setCategories(Set.of(initFirstCategory()));

        Book secondBook = new Book();
        secondBook.setId(2L);
        secondBook.setTitle("Name_of_book_2");
        secondBook.setAuthor("Author_2");
        secondBook.setIsbn("978-1-23-456789-7");
        secondBook.setPrice(BigDecimal.valueOf(10));
        secondBook.setDescription("Description_of_book_2");
        secondBook.setCoverImage("cover_2.jpg");
        secondBook.setCategories(Set.of(initSecondCategory()));

        Book thirdBook = new Book();
        thirdBook.setId(3L);
        thirdBook.setTitle("Name_of_book_3");
        thirdBook.setAuthor("Author_3");
        thirdBook.setIsbn("978-0-12-345678-6");
        thirdBook.setPrice(BigDecimal.valueOf(15));
        thirdBook.setDescription("Description_of_book_3");
        thirdBook.setCoverImage("cover_3.jpg");
        thirdBook.setCategories(Set.of(initSecondCategory()));

        return Set.of(firstBook, secondBook, thirdBook);
    }

    protected Category initFirstCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Name_of_category_1");
        category.setDescription("Description_of_category_1");
        category.setDeleted(false);

        return category;
    }

    protected Category initSecondCategory() {
        Category category = new Category();
        category.setId(2L);
        category.setName("Name_of_category_2");
        category.setDescription("Description_of_category_2");
        category.setDeleted(false);

        return category;
    }

}
