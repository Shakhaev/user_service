package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserService userService;
    @Spy
    private MentorshipRequestMapperImpl mapper;
    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;

    @Test
    public void testRequestMentorshipWithoutDescription() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterUserId(1L)
                .receiverUserId(1L)
                .build();

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipWithTheSameRequesterAndReceiverId() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterUserId(1L)
                .receiverUserId(1L)
                .description("some description")
                .build();

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipWithNonExistentRequesterUserId() {
        Mockito.when(userService.findById(1L)).thenThrow(new IllegalArgumentException());

        var requestDto = MentorshipRequestDto.builder()
                .requesterUserId(1L)
                .receiverUserId(2L)
                .description("some description")
                .build();

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipWithNonExistentReceiverUserId() {
        Mockito.when(userService.findById(1L)).thenReturn(UserDto.builder().userId(1L).build());
        Mockito.when(userService.findById(2L)).thenThrow(new IllegalArgumentException());

        var requestDto = MentorshipRequestDto.builder()
                .requesterUserId(1L)
                .receiverUserId(2L)
                .description("some description")
                .build();

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipWithTooFrequentRequest() {
        long requesterId = 1L;
        long receiverId = 2L;
        MentorshipRequest latestMentorshipRequest = new MentorshipRequest();
        latestMentorshipRequest.setId(requesterId);
        latestMentorshipRequest.setCreatedAt(LocalDateTime.now().minusDays(89));
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterUserId(1L)
                .receiverUserId(2L)
                .description("some description")
                .build();
        UserDto requester = UserDto.builder()
                .userId(1L)
                .build();
        UserDto receiver = UserDto.builder()
                .userId(2L)
                .build();
        Mockito.when(mentorshipRequestRepository.findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.of(latestMentorshipRequest));
        Mockito.when(userService.findById(1L)).thenReturn(requester);
        Mockito.when(userService.findById(2L)).thenReturn(receiver);

        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> mentorshipRequestService.requestMentorship(requestDto));
    }

    @Test
    public void testRequestMentorshipWithCorrectRequest() {
        MentorshipRequestDto requestDto = MentorshipRequestDto.builder()
                .requesterUserId(1L)
                .receiverUserId(2L)
                .description("description")
                .build();

        Mockito.when(userService.findById(1L)).thenReturn(UserDto.builder().userId(1L).build());
        Mockito.when(userService.findById(2L)).thenReturn(UserDto.builder().userId(2L).build());

        mentorshipRequestService.requestMentorship(requestDto);
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .create(1L, 2L, "description");
    }
}
