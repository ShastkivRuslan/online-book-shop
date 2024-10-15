package ruslan.shastkiv.bookstore.repository.book.specification;

import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.repository.SpecificationProvider;

public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "price";
    private static final int MIN_PRICE = 0;
    private static final int MAX_PRICE = 1;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            if (params.length == MIN_PRICE) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(KEY),
                        new BigDecimal(params[MIN_PRICE]));
            } else if (params.length == MAX_PRICE) {
                return criteriaBuilder.between(root.get(KEY),
                        new BigDecimal(params[MIN_PRICE]),
                        new BigDecimal(params[MAX_PRICE]));
            } else {
                throw new IllegalArgumentException(
                        "Expected 1 or 2 parameters for price filtering, but got: "
                        + params.length
                );
            }
        };
    }
}
