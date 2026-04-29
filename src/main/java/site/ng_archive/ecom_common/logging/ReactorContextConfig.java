package site.ng_archive.ecom_common.logging;

import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ThreadLocalAccessor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@Slf4j
@Configuration
public class ReactorContextConfig {

    @PostConstruct
    public void enableContextPropagation() {
        // TraceId ThreadLocalAccessor 등록
        ContextRegistry.getInstance().registerThreadLocalAccessor(new TraceIdThreadLocalAccessor());
        Hooks.enableAutomaticContextPropagation();
    }

    /**
     * TransactionId를 MDC와 Reactor Context 간에 전파하는 ThreadLocalAccessor
     */
    private static class TraceIdThreadLocalAccessor implements ThreadLocalAccessor<String> {

        @Override
        public Object key() {
            return TraceIdFilter.TRACE_ID;
        }

        @Override
        public String getValue() {
            return MDC.get(TraceIdFilter.TRACE_ID);
        }

        @Override
        public void setValue(String value) {
            MDC.put(TraceIdFilter.TRACE_ID, value);
        }

        @Override
        public void setValue() {
            // MDC를 명시적으로 제거하지 않음
            // MDC.remove(TraceIdFilter.TRACE_ID);
        }
    }
}