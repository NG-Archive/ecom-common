package site.ng_archive.ecom_common.logger;

import jakarta.annotation.Nonnull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class LoggingFilter implements WebFilter {

    private static final Logger log = LogManager.getLogger(LoggingFilter.class);
    private static final String TRACE_ID = "traceId";

    @Override
    @Nonnull
    public Mono<Void> filter(ServerWebExchange exchange, @Nonnull WebFilterChain chain) {
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        long start = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();

        return chain.filter(exchange)
            .contextWrite(Context.of(TRACE_ID, traceId))
            .doOnSubscribe(sub -> logRequest(request, traceId))
            .doFinally(signal -> logResponse(exchange, request, traceId, start, signal));
    }

    private void logRequest(ServerHttpRequest request, String traceId) {
        String transactionId = "[" + UUID.randomUUID() + "] : ";
        ThreadContext.put("transactionId", transactionId);
        setThreadContext(traceId, request);

        log.info("[REQUEST] [URI={}, method={}], ClientIP : {}, Headers : {}, QueryString : {}",
            request.getURI().getPath(),
            request.getMethod(),
            getClientIP(request),
            getHeaders(request),
            request.getURI().getQuery() == null ? "-" : request.getURI().getQuery()
        );
        ThreadContext.clearAll();
    }

    private void logResponse(ServerWebExchange exchange,
        ServerHttpRequest request,
        String traceId,
        long start,
        reactor.core.publisher.SignalType signal) {
        long duration = System.currentTimeMillis() - start;
        int status = Optional.ofNullable(exchange.getResponse().getStatusCode())
            .map(HttpStatusCode::value)
            .orElse(0);

        String transactionId = "[" + UUID.randomUUID() + "] : ";
        ThreadContext.put("transactionId", transactionId);
        setThreadContext(traceId, request);

        if (signal == reactor.core.publisher.SignalType.ON_ERROR) {
            log.error("[RESPONSE] [URI={}, method={}], Status : {}, Duration : {}ms, ClientIP : {}, [ERROR]",
                request.getURI().getPath(),
                request.getMethod(),
                status,
                duration,
                getClientIP(request));
        } else {
            log.info("[RESPONSE] [URI={}, method={}], Status : {}, Duration : {}ms, ClientIP : {}",
                request.getURI().getPath(),
                request.getMethod(),
                status,
                duration,
                getClientIP(request));
        }

        ThreadContext.clearAll();
    }

    private void setThreadContext(String traceId, ServerHttpRequest request) {
        ThreadContext.put(TRACE_ID, traceId);
        ThreadContext.put("method", request.getMethod().name());
        ThreadContext.put("uri", request.getURI().getPath());
    }

    private String getClientIP(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("x-forwarded-for");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeaders().getFirst("x-real-ip");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return Optional.ofNullable(request.getRemoteAddress())
            .map(addr -> addr.getAddress().getHostAddress())
            .orElse("-");
    }

    private String getHeaders(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        return headers.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
            .collect(Collectors.joining(", ", "{", "}"));
    }
}