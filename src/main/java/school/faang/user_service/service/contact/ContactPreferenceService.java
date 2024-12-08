package school.faang.user_service.service.contact;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.contact.ContactPreferenceRepository;

@Service
@RequiredArgsConstructor
public class ContactPreferenceService {
    private final ContactPreferenceRepository contactPreferenceRepository;
    private final UserRepository userRepository;

    @Transactional
    public void updatePreference(Long userId, PreferredContact contact) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + userId));

        ContactPreference contactPreference = contactPreferenceRepository.findById(userId)
                .orElse(null);

        if (contactPreference == null) {
            contactPreference = new ContactPreference();
            contactPreference.setUser(user);
        }

        contactPreference.setPreference(contact);
        contactPreferenceRepository.save(contactPreference);
    }

    public PreferredContact getUserPreference(Long userId) {
        return contactPreferenceRepository.findById(userId)
                .map(ContactPreference::getPreference)
                .orElse(null);
    }
}
