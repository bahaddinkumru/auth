package auth.auth.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^[a-zA-ZğüşıöçĞÜŞİÖÇ\\s]+$", message = "Bu alan sadece harflerden oluşmalıdır!")
public @interface OnlyChars {
    String message() default "Geçersiz karakter içeriyor!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
