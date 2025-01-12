package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.user.UserIdListIsEmptyException;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest {
    private static final Long USER_ID = 1L;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDomainService userDomainService;

    private final User userMock = mock(User.class);

    @Test
    void testSave_successful() {
        userDomainService.save(userMock);

        verify(userRepository).save(userMock);
    }

    @Test
    void testFindById_successful() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userMock));

        assertThat(userDomainService.findById(USER_ID))
                .isNotNull()
                .isInstanceOf(User.class);

        verify(userRepository).findById(USER_ID);
    }

    @Test
    void testFindById_notFound_exception() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDomainService.findById(USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(new UserNotFoundException(USER_ID).getMessage());
    }

    @Test
    void testFindAllByIds() {
        List<Long> ids = List.of(USER_ID);
        List<User> users = List.of(userMock);

        when(userRepository.findAllById(ids)).thenReturn(users);

        assertThat(userDomainService.findAllByIds(ids).get(0))
                .isInstanceOf(User.class);
    }

    @Test
    void testGetOnlyActiveUserIdsFromList_successful() {
        List<Long> ids = List.of(USER_ID);

        when(userRepository.findActiveUserIds(ids)).thenReturn(ids);

        assertThat(userDomainService.getOnlyActiveUserIdsFromList(ids).get(0))
                .isInstanceOf(Long.class);
    }

    @Test
    void testGetOnlyActiveUserIdsFromList_emptyList_exception() {
        assertThatThrownBy(() -> userDomainService.getOnlyActiveUserIdsFromList(List.of()))
                .isInstanceOf(UserIdListIsEmptyException.class);
    }

    @Test
    void testExistByEmail_successful() {
        String email = "email";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThat(userDomainService.existsByEmail(email))
                .isEqualTo(true);
    }

    @Test
    void testExistByUsername_successful() {
        String username = "username";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        assertThat(userDomainService.existsByUsername(username))
                .isEqualTo(true);
    }
}