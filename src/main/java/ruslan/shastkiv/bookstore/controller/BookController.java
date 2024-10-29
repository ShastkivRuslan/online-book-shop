package ruslan.shastkiv.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ruslan.shastkiv.bookstore.dto.book.BookDto;
import ruslan.shastkiv.bookstore.dto.book.BookSearchParametersDto;
import ruslan.shastkiv.bookstore.dto.book.CreateBookRequestDto;
import ruslan.shastkiv.bookstore.service.BookService;

@Tag(name = "Books", description = "Manage books in the bookstore")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/books")
public class BookController {
    private final BookService bookService;

    @Operation(
            summary = "Get all books",
            description = "Retrieve a list of all books. Supports pagination."
    )
    @GetMapping
    public List<BookDto> getAll(Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @Operation(
            summary = "Get book by ID",
            description = "Retrieve a specific book by its ID. "
                    + "Returns 404 if the book is not found."
    )
    @GetMapping("/{id}")
    public BookDto getBokById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @Operation(
            summary = "Create a new book",
            description = "Add a new book to the bookstore. "
                    + "The request body must include title, author, ISBN, and price."
    )
    @PostMapping
    public BookDto createBook(@RequestBody CreateBookRequestDto bookDto) {
        return bookService.createBook(bookDto);
    }

    @Operation(
            summary = "Update book",
            description = "Update an existing book. "
                    + "Requires the book ID and the updated details in the request body."
    )
    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id, @RequestBody CreateBookRequestDto requestDto) {
        return bookService.updateBook(id, requestDto);
    }

    @Operation(
            summary = "Delete a book",
            description = "Remove a book from the bookstore by its ID. "
                    + "Returns 404 if the book is not found."
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    @Operation(
            summary = "Search for books",
            description = "Search for books by author, title, or price range. "
                    + "If only one price is provided, it will be treated as the minimum price, "
                    + "and results will be returned starting from that price. "
                    + "If two prices are provided, they will be treated as "
                    + "the minimum and maximum price range.")

    @GetMapping("/search")
    public List<BookDto> search(BookSearchParametersDto searchParametersDto, Pageable pageable) {
        return bookService.search(searchParametersDto, pageable);
    }
}
