package ruslan.shastkiv.bookstore.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T, D> {
    Specification<T> build(D searchParameters);
}
