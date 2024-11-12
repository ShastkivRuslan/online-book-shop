package ruslan.shastkiv.bookstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CategoryRequestDto(@Length(max = 20)
                                 @NotBlank
                                 String name,
                                 @Length(max = 1000)
                                 String description) {
}
