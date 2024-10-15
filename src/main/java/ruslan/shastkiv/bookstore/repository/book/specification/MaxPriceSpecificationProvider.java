package ruslan.shastkiv.bookstore.repository.book.specification;

import org.springframework.data.jpa.domain.Specification;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.repository.SpecificationProvider;

import java.util.Arrays;

public class MaxPriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "price";
    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Book> getSpecification(String[] maxPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(root.get(KEY), maxPrice);
    }
}
