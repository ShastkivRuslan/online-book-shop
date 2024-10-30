package ruslan.shastkiv.bookstore.validation.password_matcher;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ruslan.shastkiv.bookstore.dto.user.UserRegistrationRequestDto;
import ruslan.shastkiv.bookstore.validation.isbn.PasswordMatcher;

public class PasswordMatcherValidator
        implements ConstraintValidator<PasswordMatcher, UserRegistrationRequestDto> {
    private static final String FIELD_NAME = "repeatPassword";
    private static final String ERROR_MESSAGE = "The entered passwords do not match.";

    @Override
    public boolean isValid(UserRegistrationRequestDto registrationRequestDto,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (!registrationRequestDto.password().equals(registrationRequestDto.repeatPassword())) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(ERROR_MESSAGE)
                    .addPropertyNode(FIELD_NAME)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
