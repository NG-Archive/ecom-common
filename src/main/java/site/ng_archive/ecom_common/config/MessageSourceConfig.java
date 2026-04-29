package site.ng_archive.ecom_common.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

    @Bean
    @Primary
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        // 프로젝트별 messages.properties를 먼저, 공통 common-messages를 나중에 로드
        // 같은 키가 있으면 먼저 정의된 것(프로젝트별)이 우선
        messageSource.setBasenames(
            "messages",           // 각 프로젝트의 messages.properties
            "common-messages"     // ecom-common의 common-messages.properties
        );

        messageSource.setDefaultEncoding("UTF-8");
        // locale별 파일이 없어도 기본 .properties 파일 사용
        messageSource.setFallbackToSystemLocale(true);

        return messageSource;
    }
}