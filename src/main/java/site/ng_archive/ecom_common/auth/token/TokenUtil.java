package site.ng_archive.ecom_common.auth.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import site.ng_archive.ecom_common.auth.UserContext;
import site.ng_archive.ecom_common.auth.exception.TokenInvalidException;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public final class TokenUtil {

    private static final String JWT_NAME = "jwt";
    private static final String CLAIM_MEMBER_ID = "memberId";
    private static final String CLAIM_MEMBER_ROLE = "role";
    private static final Long DEFAULT_EXPIRED_MINUTE = 30L;

    public static String getSign(UserContext userContext) {
        return JWT.create()
                .withSubject(JWT_NAME)
                .withExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(DEFAULT_EXPIRED_MINUTE)))
                .withClaim(CLAIM_MEMBER_ID, userContext.id())
                .withClaim(CLAIM_MEMBER_ROLE, userContext.role())
                .sign(Algorithm.HMAC512(TokenSecretKey.getJwtSecret()));
    }

    public static UserContext verify(String token) {
        DecodedJWT decoded = JWT.require(Algorithm.HMAC512(TokenSecretKey.getJwtSecret()))
            .withSubject(JWT_NAME)
            .build()
            .verify(token);

        Long memberId = decoded.getClaim(CLAIM_MEMBER_ID).asLong();
        String role = decoded.getClaim(CLAIM_MEMBER_ROLE).asString();

        if (memberId == null || role == null) {
            throw new TokenInvalidException("token.invalid");
        }

        return UserContext.of(memberId, role);
    }
}
