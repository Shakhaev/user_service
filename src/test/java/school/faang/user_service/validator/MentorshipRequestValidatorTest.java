package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestValidatorTest {
    private static final long RECEIVER_ID = 2L;
    private static final long REQUESTER_ID = 1L;
    private static final String DESCRIPTION = "описание";
    private static final long WRONG_MONTHS_REPEAT_REQUEST_LIMIT = 4;
    private static final long RIGHT_MONTHS_REPEAT_REQUEST_LIMIT = 1;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private MentorshipRequestValidator validator;

    @Test
    void shouldSuccessValidate() {
        MentorshipRequestDto dto = new MentorshipRequestDto(REQUESTER_ID, RECEIVER_ID, DESCRIPTION);
        when(mentorshipRequestRepository.findLatestRequest(any(Long.class), any(Long.class)))
                .thenReturn(Optional.empty());

        validator.validate(dto);
        verify(userService).isUserExists(REQUESTER_ID);
        verify(userService).isUserExists(RECEIVER_ID);
        verifyNoMoreInteractions(userService);
        verify(mentorshipRequestRepository).findLatestRequest(REQUESTER_ID, RECEIVER_ID);
        verifyNoMoreInteractions(mentorshipRequestRepository);
    }

    @Test
    void shouldThrowBusinessExceptionWhenSenderIsSameAsReceiver() {
        MentorshipRequestDto dto = new MentorshipRequestDto(REQUESTER_ID, REQUESTER_ID, DESCRIPTION);

        BusinessException exception = assertThrows(BusinessException.class, () -> validator.validate(dto));
        assertEquals("Нельзя отправить запрос на менторство самому себе", exception.getMessage());
        verify(userService, times(2)).isUserExists(REQUESTER_ID);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(mentorshipRequestRepository);
    }

    @Test
    void shouldThrowBusinessExceptionWhenRepeatRequestSentTooEarly() {
        MentorshipRequestDto dto = new MentorshipRequestDto(REQUESTER_ID, RECEIVER_ID, DESCRIPTION);
        MentorshipRequest latestRequest = mock(MentorshipRequest.class);
        when(latestRequest.getCreatedAt()).thenReturn(LocalDateTime.now().minusMonths(RIGHT_MONTHS_REPEAT_REQUEST_LIMIT));
        when(mentorshipRequestRepository.findLatestRequest(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(latestRequest));

        BusinessException exception = assertThrows(BusinessException.class, () -> validator.validate(dto));
        assertEquals("Запрос можно отправить раз в 3 месяца", exception.getMessage());
        verify(userService).isUserExists(REQUESTER_ID);
        verify(userService).isUserExists(RECEIVER_ID);
        verifyNoMoreInteractions(userService);
        verify(mentorshipRequestRepository).findLatestRequest(REQUESTER_ID, RECEIVER_ID);
        verifyNoMoreInteractions(mentorshipRequestRepository);
    }

    @Test
    void shouldNotThrowExceptionIfRequestWithinLimits() {
        MentorshipRequestDto dto = new MentorshipRequestDto(REQUESTER_ID, RECEIVER_ID, DESCRIPTION);
        MentorshipRequest latestRequest = mock(MentorshipRequest.class);
        when(latestRequest.getCreatedAt()).thenReturn(LocalDateTime.now().minusMonths(WRONG_MONTHS_REPEAT_REQUEST_LIMIT));
        when(mentorshipRequestRepository.findLatestRequest(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(latestRequest));

        validator.validate(dto);
        verify(userService).isUserExists(REQUESTER_ID);
        verify(userService).isUserExists(RECEIVER_ID);
        verifyNoMoreInteractions(userService);
        verify(mentorshipRequestRepository).findLatestRequest(REQUESTER_ID, RECEIVER_ID);
        verifyNoMoreInteractions(mentorshipRequestRepository);
    }

    @Test
    void shouldNotThrowExceptionWhenReceiverIsNotAMentor() {
        User requester = new User();
        requester.setId(REQUESTER_ID);
        requester.setMentors(new ArrayList<>());
        User receiver = new User();
        receiver.setId(RECEIVER_ID);
        List<User> mentors = new ArrayList<>();
        User randomUser;
        for (long i = 3; i < 5; i++) {
            randomUser = new User();
            randomUser.setId(i);
            mentors.add(randomUser);
        }
        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.getRequester().getMentors().addAll(mentors);

        validator.validateRequesterHaveReceiverAsMentor(request);
    }

    @Test
    void shouldThrowBusinessExceptionWhenReceiverIsAlreadyAMentor() {
        User requester = new User();
        requester.setId(REQUESTER_ID);
        requester.setMentors(new ArrayList<>());
        User receiver = new User();
        receiver.setId(RECEIVER_ID);
        List<User> mentors = new ArrayList<>();
        User randomUser = new User();
        randomUser.setId(RECEIVER_ID);
        mentors.add(randomUser);

        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.getRequester().getMentors().addAll(mentors);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> validator.validateRequesterHaveReceiverAsMentor(request));

        String expectedMessage = "Получатель запроса с id=" + RECEIVER_ID +
                " уже является ментором пользователя c id=" + REQUESTER_ID;
        assertEquals(expectedMessage, exception.getMessage());
    }
}