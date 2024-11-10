package ruslan.shastkiv.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @Email
        @NotEmpty
        @Size(min = 9, max = 50)
        String email,
        @NotEmpty
        @Size(min = 8)
        String password) {
}
