package ruslan.shastkiv.bookstore.dto.book;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import ruslan.shastkiv.bookstore.validation.image.CoverImage;
import ruslan.shastkiv.bookstore.validation.isbn.Isbn;

@Data
public class CreateBookRequestDto {
    @NotBlank(message = "Title cannot be null")
    private String title;

    @NotBlank(message = "Author cannot be null")
    private String author;

    @Isbn
    private String isbn;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotBlank(message = "Description cannot be null")
    private String description;

    @CoverImage
    private String coverImage;

    @NotEmpty(message = "The book must belong to at least one category.")
    private Set<Long> categoryIds = new HashSet<>();
}
