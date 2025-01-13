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
        MentorshipRequestDto dto = MentorshipRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .description(DESCRIPTION)
                .build();
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
        MentorshipRequestDto dto = MentorshipRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(REQUESTER_ID)
                .description(DESCRIPTION)
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> validator.validate(dto));
        assertEquals("Нельзя отправить запрос на менторство самому себе", exception.getMessage());
        verify(userService, times(2)).isUserExists(REQUESTER_ID);
        verifyNoMoreInteractions(userService);
        verifyNoInteractions(mentorshipRequestRepository);
    }

    @Test
    void shouldThrowBusinessExceptionWhenRepeatRequestSentTooEarly() {
        MentorshipRequestDto dto = MentorshipRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .description(DESCRIPTION)
                .build();
        MentorshipRequest latestRequest = mock(MentorshipRequest.class);
        when(latestRequest.getCreatedAt())
                .thenReturn(LocalDateTime.now().minusMonths(RIGHT_MONTHS_REPEAT_REQUEST_LIMIT));
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
        MentorshipRequestDto dto =MentorshipRequestDto.builder()
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .description(DESCRIPTION)
                .build();
        MentorshipRequest latestRequest = mock(MentorshipRequest.class);
        when(latestRequest.getCreatedAt())
                .thenReturn(LocalDateTime.now().minusMonths(WRONG_MONTHS_REPEAT_REQUEST_LIMIT));
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
        List<User> differentMentors = List.of(User.builder().id(3L).build(), User.builder().id(5L).build());
        User requester = User.builder().id(REQUESTER_ID).mentors(differentMentors).build();
        User receiver = User.builder().id(RECEIVER_ID).build();
        MentorshipRequest request = MentorshipRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .build();

        validator.validateRequesterHaveReceiverAsMentor(request);
    }

    @Test
    void shouldThrowBusinessExceptionWhenReceiverIsAlreadyAMentor() {
        User receiver = User.builder()
                .id(RECEIVER_ID)
                .mentors(new ArrayList<>())
                .build();
        User requester = User.builder()
                .id(REQUESTER_ID)
                .mentors(List.of(receiver))
                .build();
        MentorshipRequest request = MentorshipRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .build();

        BusinessException exception = assertThrows(BusinessException.class,
                () -> validator.validateRequesterHaveReceiverAsMentor(request));

        String expectedMessage = "Получатель запроса с id=" + RECEIVER_ID +
                " уже является ментором пользователя c id=" + REQUESTER_ID;
        assertEquals(expectedMessage, exception.getMessage());
    }
}