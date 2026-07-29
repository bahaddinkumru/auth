package auth.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Auth & Token Errors
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_001", "E-posta adresi veya şifre hatalı"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "Geçersiz veya işlenemeyen token"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_003", "Token süresi dolmuş"),
    TOKEN_REVOKED_REUSE_DETECTED(HttpStatus.UNAUTHORIZED, "AUTH_004",
            "Güvenlik İhlali: İptal edilmiş token kullanımı tespit edildi! Tüm oturumlarınız sonlandırıldı."),

    // User Errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "Kullanıcı bulunamadı"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_002", "Bu e-posta adresi ile zaten bir kayıt mevcut!");

    private final HttpStatus status;
    private final String code;
    private final String defaultMessage;
}
