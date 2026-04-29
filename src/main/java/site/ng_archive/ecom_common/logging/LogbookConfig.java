package site.ng_archive.ecom_common.logging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.spring.webflux.LogbookWebFilter;

@Configuration
public class LogbookConfig {

    @Bean
    @Order(-1)
    @ConditionalOnMissingBean(LogbookWebFilter.class)
    public LogbookWebFilter logbookWebFilter(Logbook logbook) {
        return new LogbookWebFilter(logbook);
    }
}