package site.ng_archive.ecom_common.logging;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Order(-2)
public class TransactionIdFilter implements WebFilter {

    public static final String TRANSACTION_ID = "transactionId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String id = UUID.randomUUID().toString();
        MDC.put(TRANSACTION_ID, id);

        return chain.filter(exchange)
            .contextWrite(ctx -> ctx.put(TRANSACTION_ID, id));
    }
}
