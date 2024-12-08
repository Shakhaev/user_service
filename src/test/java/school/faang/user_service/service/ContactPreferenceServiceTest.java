package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.contact.ContactPreferenceRepository;
import school.faang.user_service.service.contact.ContactPreferenceService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactPreferenceServiceTest {

    @Mock
    private ContactPreferenceRepository contactPreferenceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContactPreferenceService contactPreferenceService;

    private User user;
    private ContactPreference existingContactPreference;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("JohnDoe")
                .email("johndoe@example.com")
                .build();

        existingContactPreference = ContactPreference.builder()
                .id(1L)
                .user(user)
                .preference(PreferredContact.EMAIL)
                .build();
    }

    @Test
    @DisplayName("Update contact preference success")
    void updatePreference_WhenPreferenceExists_ShouldUpdatePreference() {
        PreferredContact newPreference = PreferredContact.SMS;

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(contactPreferenceRepository.findById(user.getId())).thenReturn(Optional.of(existingContactPreference));
        when(contactPreferenceRepository.save(any(ContactPreference.class))).thenReturn(existingContactPreference);

        contactPreferenceService.updatePreference(user.getId(), newPreference);

        assertThat(existingContactPreference.getPreference()).isEqualTo(newPreference);
        verify(userRepository, times(1)).findById(user.getId());
        verify(contactPreferenceRepository, times(1)).findById(user.getId());
        verify(contactPreferenceRepository, times(1)).save(existingContactPreference);
    }

    @Test
    @DisplayName("Обновление предпочтения контакта успешно, когда предпочтение не существует и создаётся новое")
    void updatePreference_WhenPreferenceDoesNotExist_ShouldCreateAndSetPreference() {
        PreferredContact newPreference = PreferredContact.TELEGRAM;

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(contactPreferenceRepository.findById(user.getId())).thenReturn(Optional.empty());
        when(contactPreferenceRepository.save(any(ContactPreference.class))).thenAnswer(invocation -> invocation.getArgument(0));

        contactPreferenceService.updatePreference(user.getId(), newPreference);

        verify(userRepository, times(1)).findById(user.getId());
        verify(contactPreferenceRepository, times(1)).findById(user.getId());
        verify(contactPreferenceRepository, times(1)).save(any(ContactPreference.class));

        ArgumentCaptor<ContactPreference> captor = ArgumentCaptor.forClass(ContactPreference.class);
        verify(contactPreferenceRepository).save(captor.capture());
        ContactPreference savedPreference = captor.getValue();

        assertThat(savedPreference.getUser()).isEqualTo(user);
        assertThat(savedPreference.getPreference()).isEqualTo(newPreference);
    }

    @Test
    @DisplayName("Update contact preference user not found")
    void updatePreference_WhenUserDoesNotExist_ShouldThrowException() {
        Long nonExistentUserId = 99L;
        PreferredContact newPreference = PreferredContact.EMAIL;

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactPreferenceService.updatePreference(nonExistentUserId, newPreference))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id " + nonExistentUserId);

        verify(userRepository, times(1)).findById(nonExistentUserId);
        verify(contactPreferenceRepository, never()).findById(anyLong());
        verify(contactPreferenceRepository, never()).save(any(ContactPreference.class));
    }

    @Test
    @DisplayName("Get contact preference success")
    void getUserPreference_WhenPreferenceExists_ShouldReturnPreference() {
        when(contactPreferenceRepository.findById(user.getId())).thenReturn(Optional.of(existingContactPreference));

        PreferredContact preference = contactPreferenceService.getUserPreference(user.getId());

        assertThat(preference).isEqualTo(existingContactPreference.getPreference());
        verify(contactPreferenceRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("Get contact preference returns null if there is no preference")
    void getUserPreference_WhenPreferenceDoesNotExist_ShouldReturnNull() {
        when(contactPreferenceRepository.findById(user.getId())).thenReturn(Optional.empty());

        PreferredContact preference = contactPreferenceService.getUserPreference(user.getId());

        assertThat(preference).isNull();
        verify(contactPreferenceRepository, times(1)).findById(user.getId());
    }
}
