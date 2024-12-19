package school.faang.user_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.context.ContactPreferenceContext;

@RequiredArgsConstructor
public class FeignContactPreferenceInterceptor implements RequestInterceptor {

    private final ContactPreferenceContext contactPreferenceContext;

    @Override
    public void apply(RequestTemplate template) {
        template.header("x-contact-preference", contactPreferenceContext.getPreference());
    }
}