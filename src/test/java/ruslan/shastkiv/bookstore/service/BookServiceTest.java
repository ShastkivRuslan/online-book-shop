package ruslan.shastkiv.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.CUSTOM_BOOK_TITLE;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.EMPTY_PAGE;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.FIRST_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.INVALID_BOOK_ID;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.NON_EXISTING_TITLE;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.PAGEABLE;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.PAGE_SIZE_1;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.createBookById;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.createBookDtoById;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.createBookDtoWithoutCategoryIds;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.createBookRequestDtoById;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.createSearchParamsDto;
import static ruslan.shastkiv.bookstore.utils.BookTestUtils.updateBookRequestDtoById;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.FIRST_CATEGORY_ID;
import static ruslan.shastkiv.bookstore.utils.CategoryTestUtils.createCategoryById;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import ruslan.shastkiv.bookstore.dto.book.BookDto;
import ruslan.shastkiv.bookstore.dto.book.BookDtoWithoutCategoryIds;
import ruslan.shastkiv.bookstore.dto.book.BookSearchParametersDto;
import ruslan.shastkiv.bookstore.dto.book.CreateBookRequestDto;
import ruslan.shastkiv.bookstore.exception.EntityNotFoundException;
import ruslan.shastkiv.bookstore.mapper.BookMapper;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.repository.book.BookRepository;
import ruslan.shastkiv.bookstore.repository.book.BookSpecificationBuilder;
import ruslan.shastkiv.bookstore.repository.category.CategoryRepository;
import ruslan.shastkiv.bookstore.service.book.BookServiceImpl;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @InjectMocks
    private BookServiceImpl bookService;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookSpecificationBuilder specificationBuilder;

    @Test
    @DisplayName("""
            Create a book with valid request and return corresponding BookDto
            """)
    void createBook_ValidCreateBookDto_ReturnBookDto() {
        CreateBookRequestDto requestDto = createBookRequestDtoById(FIRST_BOOK_ID);
        Book book = createBookById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));
        BookDto expectedBookDto = createBookDtoById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(categoryRepository.findAllById(Set.of(FIRST_CATEGORY_ID)))
                .thenReturn(List.of(createCategoryById(FIRST_CATEGORY_ID)));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expectedBookDto);

        BookDto actual = bookService.createBook(requestDto);

        assertEquals(expectedBookDto, actual);
    }

    @Test
    @DisplayName("""
            Retrieve all books as a paginated response with valid BookDto
            """)
    void getAll_ValidPage_ReturnPageWithBookDto() {
        Book book = createBookById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));
        Page<Book> bookPage = new PageImpl<>(List.of(book), PAGEABLE, PAGE_SIZE_1);
        BookDto bookDto = createBookDtoById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));

        when(bookRepository.findAllWithCategories(PAGEABLE)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> expected = new PageImpl<>(List.of(bookDto), PAGEABLE, PAGE_SIZE_1);
        Page<BookDto> actual = bookService.getAll(PAGEABLE);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Retrieve a book by ID and return corresponding BookDto
            """)
    void getBookById_ValidId_ReturnBookDto() {
        Book book = createBookById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));
        BookDto expected = createBookDtoById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));

        when(bookRepository.findById(FIRST_BOOK_ID)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expected);

        BookDto actual = bookService.getBookById(FIRST_BOOK_ID);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Attempt to retrieve a book by invalid ID should throw EntityNotFoundException
            """)
    void getBookById_InvalidId_ThrowException() {
        when(bookRepository.findById(INVALID_BOOK_ID)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookService.getBookById(INVALID_BOOK_ID));

        verifyNoInteractions(bookMapper);
    }

    @Test
    @DisplayName("""
            Update an existing book with valid request and return updated BookDto
            """)
    void updateBook_ValidRequestDto_ReturnBookDto() {
        CreateBookRequestDto requestDto = updateBookRequestDtoById(FIRST_BOOK_ID);

        Book book = createBookById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));

        when(bookRepository.findById(FIRST_BOOK_ID)).thenReturn(Optional.of(book));

        bookService.updateBook(FIRST_BOOK_ID, requestDto);

        verify(bookMapper).updateBookFromDto(requestDto, book);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("""
            Delete a book by ID and verify deletion
            """)
    void deleteBook_ValidId_VerifyDeletion() {
        bookService.deleteBook(FIRST_BOOK_ID);

        verify(bookRepository).deleteById(FIRST_BOOK_ID);

        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("""
            Search books with valid search parameters and return paginated BookDto
            """)
    void search_ValidSearchParameters_ReturnBookDtoPage() {
        BookSearchParametersDto searchParametersDto = createSearchParamsDto();

        Specification<Book> specification = (root, query, criteriaBuilder)
                -> criteriaBuilder.equal(
                        root.get("title"), CUSTOM_BOOK_TITLE.formatted(FIRST_BOOK_ID));

        Book book = createBookById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));
        Page<Book> books = new PageImpl<>(List.of(book), PAGEABLE, PAGE_SIZE_1);

        BookDto dto = createBookDtoById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));

        when(bookMapper.toDto(book)).thenReturn(dto);
        when(specificationBuilder.build(searchParametersDto)).thenReturn(specification);
        when(bookRepository.findAll(specification, PAGEABLE)).thenReturn(books);

        Page<BookDto> expected = new PageImpl<>(List.of(dto), PAGEABLE, PAGE_SIZE_1);
        Page<BookDto> actual = bookService.search(searchParametersDto, PAGEABLE);

        assertEquals(expected, actual);

        verify(specificationBuilder).build(searchParametersDto);
        verify(bookRepository).findAll(specification, PAGEABLE);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Search books with no matching parameters should return empty page")
    void search_NoMatchingParameters_ShouldReturnEmptyPage() {
        BookSearchParametersDto searchParametersDto = createSearchParamsDto();

        Specification<Book> specification = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("title"), NON_EXISTING_TITLE);

        when(specificationBuilder.build(searchParametersDto)).thenReturn(specification);
        when(bookRepository.findAll(specification, PAGEABLE)).thenReturn(Page.empty(PAGEABLE));

        Page<BookDto> actual = bookService.search(searchParametersDto, PAGEABLE);

        assertTrue(actual.isEmpty());

        verify(specificationBuilder).build(searchParametersDto);
        verify(bookRepository).findAll(specification, PAGEABLE);
        verifyNoMoreInteractions(specificationBuilder, bookRepository);
        verifyNoInteractions(bookMapper);
    }

    @Test
    @DisplayName("""
            Get all books by category ID and return corresponding BookDto without category IDs
            """)
    void getAllBooksByCategoryId_ValidCategoryId_ReturnBookDtoPage() {
        Book book = createBookById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));
        Page<Book> books = new PageImpl<>(List.of(book), PAGEABLE, PAGE_SIZE_1);

        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds
                = createBookDtoWithoutCategoryIds(FIRST_BOOK_ID);
        PageImpl<BookDtoWithoutCategoryIds> expectedPage
                = new PageImpl<>(List.of(bookDtoWithoutCategoryIds), PAGEABLE, PAGE_SIZE_1);

        when(bookRepository.findBooksByCategoryId(FIRST_BOOK_ID, PAGEABLE)).thenReturn(books);
        when(bookMapper.toDtoWithoutCategoryIds(book)).thenReturn(bookDtoWithoutCategoryIds);

        Page<BookDtoWithoutCategoryIds> actualPage
                = bookService.getAllBooksByCategoryId(FIRST_BOOK_ID, PAGEABLE);

        assertEquals(expectedPage, actualPage);
    }

    @Test
    @DisplayName("""
            Get all books by invalid category ID and return empty page
            """)
    void getAllBooksByCategoryId_InvalidCategoryId_ReturnBookDtoPage() {
        Page<Book> books = new PageImpl<>(List.of(), PAGEABLE, EMPTY_PAGE);

        when(bookRepository.findBooksByCategoryId(INVALID_BOOK_ID, PAGEABLE)).thenReturn(books);

        Page<BookDtoWithoutCategoryIds> actualPage
                = bookService.getAllBooksByCategoryId(INVALID_BOOK_ID, PAGEABLE);

        assertTrue(actualPage.isEmpty());
    }

    @Test
    @DisplayName("""
            Find a book by ID and return the corresponding Book object
            """)
    void findBookById_ValidId_ReturnBook() {
        Book book = createBookById(FIRST_BOOK_ID, List.of(FIRST_CATEGORY_ID));

        when(bookRepository.findById(FIRST_BOOK_ID)).thenReturn(Optional.of(book));

        Book actual = bookService.findBookById(FIRST_BOOK_ID);

        assertEquals(book, actual);
    }

    @Test
    @DisplayName("""
            Throw EntityNotFoundException when book ID does not exist
            """)
    void findBookById_InvalidId_ThrowException() {
        when(bookRepository.findById(INVALID_BOOK_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, ()
                -> bookService.findBookById(INVALID_BOOK_ID));
    }
}

