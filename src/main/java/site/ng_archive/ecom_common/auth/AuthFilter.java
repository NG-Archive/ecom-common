package site.ng_archive.ecom_common.auth;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import site.ng_archive.ecom_common.auth.token.TokenUtil;
import site.ng_archive.ecom_common.error.ErrorMessageUtil;
import site.ng_archive.ecom_common.error.ErrorResponse;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(-1)
@RequiredArgsConstructor
public class AuthFilter implements WebFilter {

    private final ErrorMessageUtil errorMessageUtil;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String authHeader = getAuthorization(exchange);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }
        String token = authHeader.substring(7);

        return Mono.fromCallable(() -> TokenUtil.verify(token))
            .flatMap(userContext -> chain.filter(exchange)
                .contextWrite(ctx -> ctx.put("userContext", userContext)))
            .onErrorResume(JWTVerificationException.class, e -> errorResponse(exchange, e));
    }

    private static String getAuthorization(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst("Authorization");
    }

    private Mono<Void> errorResponse(ServerWebExchange exchange, RuntimeException ex) {
        log.error("Token Unauthorized: {}", ex.getMessage());

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ErrorResponse errorBody = getErrorResponse(ex);

        return response.writeWith(
            Mono.fromCallable(() -> objectMapper.writeValueAsBytes(errorBody))
                .map(bytes -> response.bufferFactory().wrap(bytes))
                .doOnError(e -> log.error("JSON Serialization Error", e))
                .onErrorResume(e -> {
                    byte[] fallback = "{\"code\":\"INTERNAL_ERROR\",\"message\":\"Server Error\"}"
                        .getBytes(StandardCharsets.UTF_8);
                    return Mono.just(response.bufferFactory().wrap(fallback));
                })
        );
    }

    private ErrorResponse getErrorResponse(RuntimeException ex) {
        String errorCode = getErrorCode(ex);
        return errorMessageUtil.getErrorResult(errorCode);
    }

    private static String getErrorCode(RuntimeException ex) {
        return switch (ex) {
            case TokenExpiredException e -> "token.expired";
            case JWTVerificationException e -> "token.invalid";
            default ->  "token.auth.failed";
        };
    }
}
