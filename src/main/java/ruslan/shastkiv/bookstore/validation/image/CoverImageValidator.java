package ruslan.shastkiv.bookstore.validation.image;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CoverImageValidator implements ConstraintValidator<CoverImage, String> {
    private static final String PATTERN_OF_COVER_IMAGE_URL
            = "(http(s?):)([/|.|\\w|\\s|-])*\\.(?:jpg|gif|png)";

    @Override
    public boolean isValid(String url, ConstraintValidatorContext constraintValidatorContext) {
        return url != null && Pattern.compile(PATTERN_OF_COVER_IMAGE_URL).matcher(url).matches();
    }
}
