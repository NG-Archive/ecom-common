package site.ng_archive.ecom_common.logging;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.CorrelationId;
import org.zalando.logbook.spring.webflux.LogbookWebFilter;

import java.util.UUID;

@Configuration
public class LogbookConfig {

    public static final String CORRELATION_ID = "correlationId";

    @Bean
    @ConditionalOnMissingBean(CorrelationId.class)
    public CorrelationId correlationId() {
        return request -> {
            String id = UUID.randomUUID().toString();
            MDC.put(CORRELATION_ID, id);
            return id;
        };
    }

    @Bean
    @Order(-2)
    @ConditionalOnMissingBean(LogbookWebFilter.class)
    public LogbookWebFilter logbookWebFilter(Logbook logbook) {
        return new LogbookWebFilter(logbook);
    }
}