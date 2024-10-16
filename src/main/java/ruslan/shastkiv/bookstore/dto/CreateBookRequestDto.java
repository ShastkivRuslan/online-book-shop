package ruslan.shastkiv.bookstore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import ruslan.shastkiv.bookstore.validation.image.CoverImage;
import ruslan.shastkiv.bookstore.validation.isbn.Isbn;

@Data
public class CreateBookRequestDto {
    @NotNull(message = "Title cannot be null")
    private String title;

    @NotNull(message = "Author cannot be null")
    private String author;

    @Isbn
    private String isbn;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Description cannot be null")
    private String description;

    @CoverImage
    private String coverImage;
}
