package ruslan.shastkiv.bookstore.validation.image;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CoverImageValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CoverImage {
    String message() default "Cover image must be a valid URL ending with .jpg, .jpeg, or .png";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
