package ruslan.shastkiv.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import ruslan.shastkiv.bookstore.validation.isbn.PasswordMatcher;

@PasswordMatcher
public record UserRegistrationRequestDto(@NotBlank
                                         @Email
                                         String email,
                                         @NotBlank
                                         @Length(min = 8, max = 30)
                                         String password,
                                         @NotBlank
                                         @Length(min = 8, max = 30)
                                         String repeatPassword,
                                         @NotBlank
                                         String firstName,
                                         @NotBlank
                                         String lastName,
                                         String shippingAddress) {
}