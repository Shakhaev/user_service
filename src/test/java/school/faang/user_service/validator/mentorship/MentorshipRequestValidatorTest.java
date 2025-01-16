package school.faang.user_service.validator.mentorship;

import jakarta.persistence.EntityNotFoundException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {

    @Mock
    private MentorshipRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipRequestValidator requestValidator;

    @Test
    public void testRequestFoundInDB() {
        long id = 2L;
        when(requestRepository.getReferenceById(id)).thenReturn(new MentorshipRequest());

        verify(requestValidator, times(1)).validateRequestId(id);
    }

    @Test
    public void testRequestNotFoundInDB() {
        long id = 3L;
        when(requestRepository.getReferenceById(id)).thenReturn(null);

        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> requestValidator.validateRequestId(id));
    }

    @Test
    public void testUsersIdsEquals() {
        long requesterId = 7L;
        long receiverId = 7L;

        Assert.assertThrows(IllegalArgumentException.class,
                () -> requestValidator.validateUsersIdNotEqual(requesterId, receiverId));
    }

    @Test
    public void testUsersIdsNotEquals() {
        long requesterId = 5L;
        long receiverId = 7L;

        assertTrue(requestValidator.validateUsersIdNotEqual(requesterId, receiverId));
    }

    @Test
    public void testUserExists() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        verify(requestValidator, times(1)).validateUserExists(userId);
    }

    @Test
    public void testUserNotExists() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        Assert.assertThrows(EntityNotFoundException.class,
                () -> requestValidator.validateUserExists(userId));
    }

    @Test
    public void testNotValidIntervalBetweenRequests() {
        long requesterId = 5L;
        long receiverId = 7L;
        int monthsInterval = 2;
        MentorshipRequest testRequest = testDataInterval(requesterId, receiverId, monthsInterval);

        when(requestRepository.findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.of(testRequest));

        assertThrows(DataValidationException.class,
                () -> requestValidator.validateLastRequestData(requesterId, receiverId));
    }

    @Test
    public void testValidIntervalBetweenRequests() {
        long requesterId = 5L;
        long receiverId = 7L;
        int monthsInterval = 4;
        MentorshipRequest testRequest = testDataInterval(requesterId, receiverId, monthsInterval);

        when(requestRepository.findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.of(testRequest));

        assertTrue(requestValidator.validateLastRequestData(requesterId, receiverId));
    }

    @Test
    public void testIsMentorAlready() {
        User firstUser = User.builder().id(1L).build();
        User secondUser = User.builder().id(2L).build();
        when(firstUser.getMentors().contains(secondUser)).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> requestValidator.validateNotMentorYet(firstUser, secondUser));
    }

    @Test
    public void testNotInMentorsList() {
        User firstUser = User.builder().id(1L).build();
        User secondUser = User.builder().id(2L).build();
        when(firstUser.getMentors().contains(secondUser)).thenReturn(false);

        assertTrue(requestValidator.validateNotMentorYet(firstUser, secondUser));
    }

    private MentorshipRequest testDataInterval(long requesterId, long receiverId, int monthsTime) {
        User firstUser = User.builder().id(requesterId).build();
        User secondUser = User.builder().id(receiverId).build();
        LocalDateTime testTme = LocalDateTime.now().minusMonths(monthsTime);

        return MentorshipRequest
                .builder()
                .receiver(firstUser)
                .requester(secondUser)
                .createdAt(testTme)
                .build();
    }

}
