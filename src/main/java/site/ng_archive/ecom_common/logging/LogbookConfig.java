package site.ng_archive.ecom_common.logging;

import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.CorrelationId;

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
}