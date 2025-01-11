package school.faang.user_service.service.mentorship_request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.exception.mentorship_request.EmptyMentorshipRequestDescriptionException;
import school.faang.user_service.exception.mentorship_request.MentorshipRequestWasAcceptedBeforeException;
import school.faang.user_service.exception.mentorship_request.NotEnoughTimeAfterLastRequestException;
import school.faang.user_service.exception.mentorship_request.UserRequestToHimselfException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestParametersCheckerTest {
    private static final int REQUEST_LIMIT_TIMES = 1;
    private static final int REQUEST_LIMIT_PERIOD = 3;
    private static final String REQUEST_LIMIT_PERIOD_TYPE = "month";

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipRequestParametersChecker checker;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(checker, "requestLimitTimes", REQUEST_LIMIT_TIMES);
        ReflectionTestUtils.setField(checker, "requestLimitPeriod", REQUEST_LIMIT_PERIOD);
        ReflectionTestUtils.setField(checker, "requestLimitPeriodType", REQUEST_LIMIT_PERIOD_TYPE);
    }

    @Test
    void testUserSendRequestToHimself() {
        long requesterId = 1L;
        long receiverId = 1L;
        String description = "description";

        RuntimeException exception = assertException(requesterId, receiverId, description);
        assertEquals(new UserRequestToHimselfException(requesterId).getMessage(), exception.getMessage());
    }

    @Test
    void testDescriptionIsNull() {
        long requesterId = 1L;
        long receiverId = 2L;
        String description = "  ";

        RuntimeException exception = assertException(requesterId, receiverId, description);
        assertEquals(new EmptyMentorshipRequestDescriptionException().getMessage(), exception.getMessage());
    }

    @Test
    void testRequesterNotFound() {
        long requesterId = 1L;
        long receiverId = 2L;
        String description = "description";
        whenExistById(requesterId, false);
        whenExistById(receiverId, true);

        RuntimeException exception = assertException(requesterId, receiverId, description);
        String expected = new UserNotFoundException(requesterId).getMessage();
        assertEquals(expected, exception.getMessage());
    }

    @Test
    void testReceiverNotFound() {
        long requesterId = 1L;
        long receiverId = 2L;
        String description = "description";
        whenExistById(requesterId, true);
        whenExistById(receiverId, false);

        RuntimeException exception = assertException(requesterId, receiverId, description);
        String expected = new UserNotFoundException(receiverId).getMessage();
        assertEquals(expected, exception.getMessage());
    }

    @Test
    void testRequestWithinTheLastThreeMonths() {
        long requesterId = 1L;
        long receiverId = 2L;
        String description = "description";
        whenExistById(requesterId, true);
        whenExistById(receiverId, true);
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        LocalDateTime minusTwoMonth = LocalDateTime.now().minusMonths(2);
        mentorshipRequest.setCreatedAt(minusTwoMonth);
        whenFindLatestRequest(mentorshipRequest, requesterId, receiverId);

        RuntimeException exception = assertException(requesterId, receiverId, description);
        assertEquals(new NotEnoughTimeAfterLastRequestException("mentoring", REQUEST_LIMIT_TIMES,
                REQUEST_LIMIT_PERIOD + " " + REQUEST_LIMIT_PERIOD_TYPE).getMessage(), exception.getMessage());
    }

    @Test
    void testExistAcceptedRequest() {
        long requesterId = 1L;
        long receiverId = 2L;
        when(mentorshipRequestRepository
                .existAcceptedRequest(requesterId, receiverId))
                .thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> checker.checkExistAcceptedRequest(requesterId, receiverId));
        String expected = new MentorshipRequestWasAcceptedBeforeException(requesterId, receiverId).getMessage();
        assertEquals(expected, exception.getMessage());
    }

    private RuntimeException assertException(long requesterId, long receiverId, String description) {
        return assertThrows(RuntimeException.class,
                () -> checker.checkRequestParams(requesterId, receiverId, description));
    }

    private void whenExistById(long id, boolean exist) {
        lenient().when(userRepository
                        .existsById(id))
                .thenReturn(exist);
    }

    private void whenFindLatestRequest(MentorshipRequest mentorshipRequest, long requesterId, long receiverId) {
        when(mentorshipRequestRepository
                .findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.of(mentorshipRequest));
    }
}