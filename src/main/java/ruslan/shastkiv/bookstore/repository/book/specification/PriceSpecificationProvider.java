package ruslan.shastkiv.bookstore.repository.book.specification;

import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.repository.SpecificationProvider;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String PRICE_COLUMN = "price";

    private static final int MIN_PRICE = 0;
    private static final int MAX_PRICE = 1;

    private static final int ONLY_MIN_PRICE = 1;
    private static final int MIN_AND_MAX_PRICE = 2;

    @Override
    public String getKey() {
        return PRICE_COLUMN;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            if (params.length == ONLY_MIN_PRICE) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(PRICE_COLUMN),
                        new BigDecimal(params[MIN_PRICE]));
            } else if (params.length == MIN_AND_MAX_PRICE) {
                return criteriaBuilder.between(root.get(PRICE_COLUMN),
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
