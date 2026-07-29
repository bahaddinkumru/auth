package auth.auth.model.dto.request;

import auth.auth.validator.OnlyChars;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
    @NotBlank(message = "Ad alanı boş olamaz!")
    @OnlyChars(message = "Ad alanı sadece harflerden oluşmalıdır!")
    private String name;

    @NotBlank(message = "Soyad alanı boş olamaz!")
    @OnlyChars(message = "Soyad alanı sadece harflerden oluşmalıdır ve boşluk içermemelidir!")
    private String surname;

    @Email(message = "Geçerli bir e-posta adresi giriniz!")
    @NotBlank(message = "E-posta boş olamaz!")
    private String email;

    @NotBlank(message = "Şifre alanı boş olamaz!")
    @Size(min = 8, max = 32, message = "Şifre en az 8, en fazla 32 karakter olmalıdır!")
    private String password;
}
