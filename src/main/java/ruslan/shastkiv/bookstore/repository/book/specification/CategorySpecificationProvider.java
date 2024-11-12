package ruslan.shastkiv.bookstore.repository.book.specification;

import jakarta.persistence.criteria.Join;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.model.Category;
import ruslan.shastkiv.bookstore.repository.SpecificationProvider;

@Component
public class CategorySpecificationProvider implements SpecificationProvider<Book> {
    private static final String CATEGORIES_COLUMN = "categories";
    private static final String ID_COLUMN = "id";

    @Override
    public String getKey() {
        return CATEGORIES_COLUMN;
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        List<Long> categoryIds = Arrays.stream(params).map(Long::valueOf).toList();
        return (root, query, criteriaBuilder) -> {
            Join<Book, Category> categoriesJoin = root.join(CATEGORIES_COLUMN);
            return criteriaBuilder.in(categoriesJoin.get(ID_COLUMN)).value(categoryIds);
        };
    }
}
