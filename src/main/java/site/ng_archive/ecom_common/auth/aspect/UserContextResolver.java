package site.ng_archive.ecom_common.auth.aspect;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.ng_archive.ecom_common.auth.UserContext;
import site.ng_archive.ecom_common.auth.exception.AccessDeniedException;

@Component
public class UserContextResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter,
        BindingContext bindingContext,
        ServerWebExchange exchange) {
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey("userContext")) {
                UserContext userContext = ctx.get("userContext");
                return Mono.just(userContext);
            }
            return Mono.error(new AccessDeniedException("auth.unauthorized"));
        });
    }
}
