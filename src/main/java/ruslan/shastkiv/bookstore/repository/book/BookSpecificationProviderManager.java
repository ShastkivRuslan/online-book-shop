package ruslan.shastkiv.bookstore.repository.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ruslan.shastkiv.bookstore.model.Book;
import ruslan.shastkiv.bookstore.repository.SpecificationProvider;
import ruslan.shastkiv.bookstore.repository.SpecificationProviderManager;

@Component
@RequiredArgsConstructor
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
               .filter(p -> p.getKey().equals(key))
               .findFirst()
               .orElseThrow(()
                       -> new RuntimeException("Can`t find correct specification by key: " + key));
    }
}
