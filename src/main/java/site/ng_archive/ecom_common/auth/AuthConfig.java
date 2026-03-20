package site.ng_archive.ecom_common.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import site.ng_archive.ecom_common.auth.aspect.UserContextResolver;

@Configuration
public class AuthConfig implements WebFluxConfigurer {
    private final UserContextResolver userContextResolver;

    public AuthConfig(UserContextResolver userContextResolver) {
        this.userContextResolver = userContextResolver;
    }

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(userContextResolver);
    }
}
