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
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book modernJava = new Book();
                modernJava.setTitle("Modern Java in action");
                modernJava.setAuthor("Alan Mycroft");
                modernJava.setPrice(BigDecimal.valueOf(999));
                bookService.save(modernJava);
                System.out.println(bookService.findAll());
            }
        };
    }

}
