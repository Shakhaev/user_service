package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.bouncycastle.cert.ocsp.Req;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorshiprequest.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @Mock
    private UserService userService;

    private MentorshipRequestFilter mentorshipRequestFilter;
    private List<MentorshipRequestFilter> mentorshipRequests;

    @BeforeEach
    public void setUp() {
        mentorshipRequestFilter = Mockito.mock(MentorshipRequestFilter.class);
        mentorshipRequests = List.of(mentorshipRequestFilter);

        mentorshipRequestService = new MentorshipRequestService(
                mentorshipRequestRepository,
                mentorshipRequestMapper,
                userService,
                mentorshipRequests
        );
    }

    @Test
    public void testRequestMentorship() {
        // arrange
        long firstUserId = 1L;
        long secondUserId = 2L;

        User firstUser = User.builder()
                .id(firstUserId)
                .build();
        User secondUser = User.builder()
                .id(secondUserId)
                .build();

        MentorshipRequestDto mentorshipRequestDto = MentorshipRequestDto.builder()
                .requesterId(firstUserId)
                .receiverId(secondUserId)
                .build();

        MentorshipRequest mentorshipRequest = new MentorshipRequest();

        when(mentorshipRequestMapper.toMentorshipRequest(mentorshipRequestDto))
                .thenReturn(mentorshipRequest);
        when(userService.getUserById(firstUserId))
                .thenReturn(firstUser);
        when(userService.getUserById(secondUserId))
                .thenReturn(secondUser);

        // act
        ArgumentCaptor<MentorshipRequest> mentorshipRequestArgumentCaptor
                = ArgumentCaptor.forClass(MentorshipRequest.class);
        mentorshipRequestService.requestMentorship(mentorshipRequestDto);

        // assert
        verify(mentorshipRequestRepository).save(mentorshipRequestArgumentCaptor.capture());
        MentorshipRequest savedMentorshipRequest = mentorshipRequestArgumentCaptor.getValue();

        assertEquals(firstUser, savedMentorshipRequest.getRequester());
        assertEquals(secondUser, savedMentorshipRequest.getReceiver());
        assertEquals(RequestStatus.PENDING, savedMentorshipRequest.getStatus());
    }

    @Test
    public void testGetRequests() {
        // arrange
        MentorshipRequest firstRequest = MentorshipRequest.builder().build();
        MentorshipRequest secondRequest = MentorshipRequest.builder().build();
        MentorshipRequest thirdRequest = MentorshipRequest.builder().build();
        List<MentorshipRequest> mentorshipRequests = List.of(
                firstRequest,
                secondRequest,
                thirdRequest
        );

        RequestFilterDto filterDto = RequestFilterDto.builder().build();

        when(mentorshipRequestRepository.findAll()).thenReturn(mentorshipRequests);

        // act
        mentorshipRequestService.getRequests(filterDto);

        // verify
        verify(mentorshipRequestMapper).toMentorshipRequestDto(mentorshipRequests);
    }

    @Test
    public void testAcceptRequest() {
        // arrange
        long requestId = 5L;
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        when(mentorshipRequestRepository.findById(requestId))
                .thenReturn(Optional.of(mentorshipRequest));

        // act
        mentorshipRequestService.acceptRequest(requestId);

        // assert
        assertEquals(RequestStatus.ACCEPTED, mentorshipRequest.getStatus());
    }

    @Test
    public void testRejectRequest() {
        // arrange
        long requestId = 5L;
        String reason = "some reason";
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        RejectionDto rejectionDto = new RejectionDto(reason);
        when(mentorshipRequestRepository.findById(requestId))
                .thenReturn(Optional.of(mentorshipRequest));

        // act
        mentorshipRequestService.rejectRequest(requestId, rejectionDto);

        // assert
        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals(reason, mentorshipRequest.getRejectionReason());
    }
}
