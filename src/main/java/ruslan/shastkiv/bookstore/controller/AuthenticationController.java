package ruslan.shastkiv.bookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ruslan.shastkiv.bookstore.dto.user.UserDto;
import ruslan.shastkiv.bookstore.dto.user.UserRegistrationRequestDto;
import ruslan.shastkiv.bookstore.exception.RegistrationException;
import ruslan.shastkiv.bookstore.service.user.UserService;

@Tag(name = "Authentication",
        description = "Endpoints for managing user authentication and registration")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;

    @Operation(summary = "Register a new user",
            description = "Registers a new user with provided credentials. "
                    + "Ensures password validation and stores user data in the system.")
    @PostMapping("/registration")
    public UserDto register(@RequestBody @Valid UserRegistrationRequestDto registrationRequestDto)
            throws RegistrationException {
        return userService.register(registrationRequestDto);
    }
}
