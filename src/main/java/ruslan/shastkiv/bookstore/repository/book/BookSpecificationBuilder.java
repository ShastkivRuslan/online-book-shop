package ruslan.shastkiv.bookstore.repository.book;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ruslan.shastkiv.bookstore.dto.book.BookSearchParametersDto;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.repository.SpecificationBuilder;
import ruslan.shastkiv.bookstore.repository.SpecificationProviderManager;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements
        SpecificationBuilder<Book,
        BookSearchParametersDto> {
    private static final String AUTHOR_KEY = "author";
    private static final String TITLE_KEY = "title";
    private static final String PRICE_KEY = "price";
    private static final String CATEGORY_KEY = "categories";

    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> specification = Specification.where(null);

        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(AUTHOR_KEY)
                    .getSpecification(searchParameters.authors())
            );
        }
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(TITLE_KEY)
                    .getSpecification(searchParameters.titles())
            );
        }
        if (searchParameters.prices() != null && searchParameters.prices().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(PRICE_KEY)
                    .getSpecification(searchParameters.prices())
            );
        }
        if (searchParameters.categories() != null && searchParameters.categories().length > 0) {
            specification = specification.and(bookSpecificationProviderManager
                    .getSpecificationProvider(CATEGORY_KEY)
                    .getSpecification(searchParameters.categories())
            );
        }
        return specification;
    }
}
