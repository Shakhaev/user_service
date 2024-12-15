package school.faang.user_service.service.contact;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.event.ContactPreferenceUpdateEvent;
import school.faang.user_service.repository.contact.ContactPreferenceRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactPreferenceService {
    private final ContactPreferenceRepository contactPreferenceRepository;

    @Transactional
    public void updatePreference(User user, PreferredContact contact) {

        ContactPreference contactPreference = contactPreferenceRepository.findById(user.getId())
                .orElseGet(() -> {
                    ContactPreference newPreference = new ContactPreference();
                    newPreference.setUser(user);
                    return newPreference;
                });

        contactPreference.setPreference(contact);
        contactPreferenceRepository.save(contactPreference);

        log.info("Updated contact preference for userId={} to {}", user.getId(), contact);
    }

    @EventListener
    @Transactional
    public void handleContactPreferenceUpdate(ContactPreferenceUpdateEvent event) {
        Long userId = event.getUserId();
        PreferredContact newPreference = event.getNewPreference();
        Long currentUserId = event.getCurrentUserId();

        log.info("Contact preference updated for userId={} by currentUserId={} to {}",
                userId, currentUserId, newPreference);
    }

    public PreferredContact getUserPreference(Long userId) {
        return contactPreferenceRepository.findById(userId)
                .map(ContactPreference::getPreference)
                .orElse(null);
    }
}
