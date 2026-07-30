package auth.auth.exception;

import java.net.URI;
import org.springframework.security.access.AccessDeniedException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(BusinessException.class)
        public ProblemDetail handleBusinessException(BusinessException ex) {
                ErrorCode errorCode = ex.getErrorCode();

                log.warn("İş Kuralı İhlali [{}]: {}", errorCode.getCode(), ex.getMessage());

                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                errorCode.getStatus(),
                                ex.getMessage());

                problemDetail.setType(URI.create("https://api.auth.com/errors/" + errorCode.getCode()));
                problemDetail.setTitle(errorCode.name());
                problemDetail.setProperty("code", errorCode.getCode());
                problemDetail.setProperty("timestamp", Instant.now().toString());

                return problemDetail;
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                log.warn("Validasyon Hatası: {}", errors);

                Map<String, Object> body = new HashMap<>();
                body.put("timestamp", Instant.now().toString());
                body.put("status", HttpStatus.BAD_REQUEST.value());
                body.put("error", "Validation Error");
                body.put("errors", errors);

                return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Exception.class)
        public ProblemDetail handleGenericException(Exception ex) {
                log.error("Sistemde Beklenmeyen Kritik Hata: ", ex);

                ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                ex.getMessage() != null ? ex.getMessage() : "Beklenmeyen bir sunucu hatası oluştu");

                problemDetail.setTitle("INTERNAL_SERVER_ERROR");
                problemDetail.setProperty("code", "SYS_500");
                problemDetail.setProperty("timestamp", Instant.now().toString());

                return problemDetail;
        }

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex,
                        HttpServletRequest request) {

                Map<String, Object> body = new HashMap<>();
                body.put("detail", ex.getMessage());
                body.put("instance", request.getRequestURI());
                body.put("status", HttpStatus.NOT_FOUND.value());
                body.put("title", HttpStatus.NOT_FOUND.getReasonPhrase());
                body.put("code", "SYS_404");
                body.put("timestamp", Instant.now().toString());

                return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex,
                        HttpServletRequest request) {
                Map<String, Object> body = new HashMap<>();

                body.put("detail", ex.getMessage());
                body.put("instance", request.getRequestURI());
                body.put("status", HttpStatus.FORBIDDEN.value());
                body.put("title", HttpStatus.FORBIDDEN.getReasonPhrase());
                body.put("code", "SYS_403");
                body.put("timestamp", Instant.now().toString());

                return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
        }
}