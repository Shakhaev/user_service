package school.faang.user_service.config.context.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.config.context.ContactPreferenceContext;
import school.faang.user_service.config.context.UserContext;

@Configuration
public class FeignConfig {

    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext) {
        return new FeignUserInterceptor(userContext);
    }

    @Bean
    public FeignContactPreferenceInterceptor feignContactPreferenceInterceptor(ContactPreferenceContext contactPreferenceContext) {
        return new FeignContactPreferenceInterceptor(contactPreferenceContext);
    }
}
