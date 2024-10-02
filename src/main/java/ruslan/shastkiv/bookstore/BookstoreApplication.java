package ruslan.shastkiv.bookstore;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.service.BookService;

@SpringBootApplication
public class BookstoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Modern Java in action");
            book.setAuthor("Alan Mycroft");
            book.setIsbn("9781638356974");
            book.setPrice(BigDecimal.valueOf(999));

            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }
}
