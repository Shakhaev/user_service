package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filters.RequestFilter;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @InjectMocks
    MentorshipRequestService requestService;

    @Spy
    private MentorshipRequestMapperImpl requestMapper;

    @Mock
    private MentorshipRequestRepository requestRepository;

    @Mock
    private MentorshipRequestValidator requestValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<RequestFilter> requestFilters;

    private MentorshipRequest requestEntity;
    private MentorshipRequestDto requestDto;
    private User requester;
    private User receiver;

    @BeforeEach
    public void initTestData() {
        long requesterId = 5L;
        long receiverId = 7L;
        User requester = User.builder().id(requesterId).build();
        User receiver = User.builder().id(receiverId).build();
        LocalDateTime testTme = LocalDateTime.now().minusMonths(5);

        MentorshipRequestDto requestDto = MentorshipRequestDto
                .builder()
                .id(1L)
                .description("mentorship request")
                .requesterId(5L)
                .receiverId(7L)
                .rejectionReason("test")
                .status(RequestStatus.PENDING)
                .build();
    }

    @Test
    public void testRequestMentorship() {

        Mockito.when(requestValidator.validateRequestForMentorship(
                requestDto.getRequesterId(), requestDto.getReceiverId()))
                .thenReturn(true);

        Mockito.when(userRepository.findById(requestDto.getRequesterId()))
                .thenReturn(Optional.of(requester));
        Mockito.when(userRepository.findById(requestDto.getReceiverId()))
                .thenReturn(Optional.of(receiver));


        requestService.requestMentorship(requestDto);

        Mockito.verify(requestEntity, Mockito.times(1)).setRequester(requester);
        Mockito.verify(requestEntity, Mockito.times(1)).setReceiver(receiver);

        Mockito.doNothing().when(requester.getSentMentorshipRequests().add(requestEntity));

        Mockito.verify(userRepository, Mockito.times(1)).save(requester);
        Mockito.verify(requestRepository, Mockito.times(1)).save(requestEntity);

    }

    @Test
    public void testGetRequestMentorship() {
    }

    @Test
    public void testAcceptRequestMentorship() {}

    @Test
    public void testRejectRequestMentorship() {}
}
