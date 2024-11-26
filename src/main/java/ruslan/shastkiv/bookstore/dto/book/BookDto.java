package ruslan.shastkiv.bookstore.dto.book;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ruslan.shastkiv.bookstore.dto.category.CategoryDto;

@Getter
@Setter
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private String description;
    private String coverImage;
    private Set<CategoryDto> categories;
}
