package ruslan.shastkiv.bookstore.repository.book.specification;

import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.repository.SpecificationProvider;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    private static final String TITLE_COLUMN = "title";

    @Override
    public String getKey() {
        return TITLE_COLUMN;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> root.get(TITLE_COLUMN).in(Arrays.stream(params).toArray());
    }
}
