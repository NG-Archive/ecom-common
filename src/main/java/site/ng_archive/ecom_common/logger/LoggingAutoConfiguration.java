package site.ng_archive.ecom_common.logger;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.WebFilter;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(WebFilter.class)
public class LoggingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LoggingFilter.class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }
}