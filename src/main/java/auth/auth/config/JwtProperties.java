package auth.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    private Access access = new Access();
    private Refresh refresh = new Refresh();

    @Data
    public static class Access {
        private String secret;
        private long expirationMs = 900000;
    }

    @Data
    public static class Refresh {
        private String secret;
        private long expirationMs = 604800000;
        private long expirationDays = 7;
    }
}
