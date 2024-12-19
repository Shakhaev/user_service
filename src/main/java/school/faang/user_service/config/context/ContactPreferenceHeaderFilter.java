package school.faang.user_service.config.context;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ContactPreferenceHeaderFilter implements Filter {

    private final ContactPreferenceContext contactPreferenceContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        String preference = req.getHeader("x-contact-preference");
        if (preference != null) {
            contactPreferenceContext.setPreference(preference);
        }
        try {
            chain.doFilter(request, response);
        } finally {
            contactPreferenceContext.clear();
        }
    }
}