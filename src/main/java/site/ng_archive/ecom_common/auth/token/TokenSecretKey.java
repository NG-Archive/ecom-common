package site.ng_archive.ecom_common.auth.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenSecretKey {
    private static String JWT_SECRET;

    public static String getJwtSecret() {
        return JWT_SECRET;
    }

    @Value("${token.jwt.secret}")
    private void setJwtSecret(String value) {
        JWT_SECRET = value;
    }
}
