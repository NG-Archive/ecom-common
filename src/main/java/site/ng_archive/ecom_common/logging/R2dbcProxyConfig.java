package site.ng_archive.ecom_common.logging;

import io.r2dbc.proxy.ProxyConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
@ConditionalOnClass({ConnectionFactory.class, ProxyConnectionFactory.class})
public class R2dbcProxyConfig {

    @Bean
    public static BeanPostProcessor r2dbcProxyBeanPostProcessor() {
        return new R2dbcConnectionFactoryPostProcessor();
    }

    static class R2dbcConnectionFactoryPostProcessor implements BeanPostProcessor, Ordered {

        private final AtomicBoolean registered = new AtomicBoolean(false);

        @Override
        public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
            if (bean instanceof ConnectionFactory cf
                    && !Proxy.isProxyClass(bean.getClass())
                    && registered.compareAndSet(false, true)) {
                return ProxyConnectionFactory.builder(cf)
                        .listener(new QueryLoggingListener())
                        .build();
            }
            return bean;
        }

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }
    }
}
