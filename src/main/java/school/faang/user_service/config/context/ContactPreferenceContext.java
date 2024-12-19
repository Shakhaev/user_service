package school.faang.user_service.config.context;

import org.springframework.stereotype.Component;

@Component
public class ContactPreferenceContext {

    private final ThreadLocal<String> preferenceHolder = new ThreadLocal<>();

    public void setPreference(String preference) {
        preferenceHolder.set(preference);
    }

    public String getPreference() {
        String preference = preferenceHolder.get();
        if (preference == null) {
            throw new IllegalArgumentException("Contact preference is missing. Please ensure the 'x-contact-preference' header is included in the request.");
        }
        return preference;
    }

    public void clear() {
        preferenceHolder.remove();
    }
}