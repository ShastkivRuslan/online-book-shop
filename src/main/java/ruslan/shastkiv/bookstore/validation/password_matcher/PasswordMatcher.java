package ruslan.shastkiv.bookstore.validation.isbn;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ruslan.shastkiv.bookstore.validation.password_matcher.PasswordMatcherValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordMatcherValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatcher {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
