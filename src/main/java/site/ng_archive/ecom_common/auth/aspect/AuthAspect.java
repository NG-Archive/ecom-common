package site.ng_archive.ecom_common.auth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;
import site.ng_archive.ecom_common.auth.UserContext;
import site.ng_archive.ecom_common.auth.exception.AccessDeniedException;
import site.ng_archive.ecom_common.auth.exception.ForbiddenException;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class AuthAspect {

    @Around("@annotation(requireRoles)")
    public Object checkAuth(ProceedingJoinPoint joinPoint, RequireRoles requireRoles) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> returnType = signature.getReturnType();

        // 1. Mono 타입인 경우
        if (Mono.class.isAssignableFrom(returnType)) {
            return Mono.deferContextual(ctx -> {
                // 권한 체크 먼저 실행
                if (!validate(ctx, requireRoles)) {
                    return Mono.error(new ForbiddenException("auth.forbidden"));
                }
                try {
                    // 비즈니스 로직(proceed) 실행
                    return (Mono<?>) joinPoint.proceed();
                } catch (Throwable e) {
                    return Mono.error(e);
                }
            });
        }

        // 2. Flux 타입인 경우
        if (Flux.class.isAssignableFrom(returnType)) {
            return Flux.deferContextual(ctx -> {
                if (!validate(ctx, requireRoles)) {
                    return Flux.error(new ForbiddenException("auth.forbidden"));
                }
                try {
                    return (Flux<?>) joinPoint.proceed();
                } catch (Throwable e) {
                    return Flux.error(e);
                }
            });
        }

        // 3. 그 외 타입(일반 타입)은 예외 발생시켜 차단
        throw new UnsupportedOperationException("error.unsupported.operation");
    }

    private boolean validate(ContextView ctx, RequireRoles requireRoles) {
        // 인증 정보 존재 여부 확인
        if (!ctx.hasKey("userContext")) {
            throw new AccessDeniedException("auth.unauthorized");
        }

        UserContext user = ctx.get("userContext");
        String[] requiredRoles = requireRoles.roles();

        // 요구하는 권한이 없으면 role만 가지고 있으면 통과
        if (requiredRoles == null || requiredRoles.length == 0) {
            return Objects.nonNull(user.role());
        }

        if (user.role() == null) return false;

        // 유저의 권한 중 하나라도 일치하는지 확인
        return Arrays.stream(requiredRoles)
            .anyMatch(role -> user.role().equalsIgnoreCase(role));
    }
}
