package ruslan.shastkiv.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import ruslan.shastkiv.bookstore.validation.matcher.PasswordMatcher;

@PasswordMatcher
public record UserRegistrationRequestDto(@NotBlank
                                         @Email
                                         @Length(max = 50)
                                         String email,
                                         @NotBlank
                                         @Length(min = 8, max = 30)
                                         String password,
                                         @NotBlank
                                         @Length(min = 8, max = 30)
                                         String repeatPassword,
                                         @NotBlank
                                         @Length(max = 30)
                                         String firstName,
                                         @NotBlank
                                         @Length(max = 30)
                                         String lastName,
                                         String shippingAddress) {
}
