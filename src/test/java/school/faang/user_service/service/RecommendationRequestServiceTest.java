package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationRequestRcvDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;

    @Captor
    private ArgumentCaptor<RecommendationRequest> recommendationRequestCaptor;

    private User requester;
    private User receiver;
    private Skill skill1;
    private Skill skill2;
    private Skill skill3;
    private RecommendationRequest recommendationRequest;
    private SkillRequest skillRequest1;
    private SkillRequest skillRequest2;
    private SkillRequest skillRequest3;
    private RecommendationRequestRcvDto recommendationRequestRcvDto;
    private RejectionDto rejectionDto;

    @BeforeEach
    void setUp() {
        requester = createUser(1L, "Requester");
        receiver = createUser(2L, "Receiver");
        skill1 = createSkill(1L, "Java");
        skill2 = createSkill(2L, "Kotlin");
        skill3 = createSkill(3L, "Hibernate");

        recommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .message("Please confirm my skills")
                .build();

        skillRequest1 = createSkillRequest(1L, recommendationRequest, skill1);
        skillRequest2 = createSkillRequest(2L, recommendationRequest, skill2);
        skillRequest3 = createSkillRequest(3L, recommendationRequest, skill3);

        recommendationRequestRcvDto = createRequestRcvDto(requester, receiver, recommendationRequest,
                Arrays.asList(1L, 2L, 3L));

        rejectionDto = RejectionDto.builder()
                .reason("Can't confirm.")
                .build();
    }

    @Test
    @DisplayName("Create Recommendation Request Successfully")
    void testCreateRecommendationRequestSuccessfully() {
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.empty());
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenAnswer(invocation -> {
                    RecommendationRequest savedRequest = invocation.getArgument(0);
                    savedRequest.setId(1L);
                    return savedRequest;
                });
        when(skillRequestRepository.create(1L, 1L)).thenReturn(skillRequest1);
        when(skillRequestRepository.create(1L, 2L)).thenReturn(skillRequest2);
        when(skillRequestRepository.create(1L, 3L)).thenReturn(skillRequest3);

        RecommendationRequestDto requestFromDB = recommendationRequestService.create(recommendationRequestRcvDto);

        verifyNoMoreInteractions(userRepository, recommendationRequestRepository, skillRepository);
        verify(recommendationRequestRepository, Mockito.times(1))
                .save(recommendationRequestCaptor.capture());
        assertEquals(recommendationRequestRcvDto.getMessage(), recommendationRequestCaptor.getValue().getMessage());

        assertNotNull(requestFromDB);
        assertEquals(1L, requestFromDB.getId());
        assertEquals(recommendationRequestRcvDto.getRequesterId(), requestFromDB.getRequesterId());
        assertEquals(recommendationRequestRcvDto.getReceiverId(), requestFromDB.getReceiverId());
        assertEquals(recommendationRequestRcvDto.getMessage(), requestFromDB.getMessage());
        assertEquals(RequestStatus.PENDING, requestFromDB.getStatus());
        assertEquals(recommendationRequestRcvDto.getSkillIds(), requestFromDB.getSkillIds());
    }

    @Test
    @DisplayName("UserRequestHimself")
    void testCreateRecommendationRequest_UserRequestHimself() {
        RecommendationRequestRcvDto requestDto = createRequestRcvDto(requester, requester, recommendationRequest,
                Arrays.asList(1L, 2L, 3L));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(requestDto));

        assertEquals("The user cannot send a request to himself", exception.getMessage());
    }

    @Test
    @DisplayName("RequestPeriodIsNotExceeded")
    void testCreateRecommendationRequest_RequestPeriodIsNotExceeded() {
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.of(RecommendationRequest.builder()
                        .createdAt(LocalDateTime.now().minusMonths(3))
                        .build()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(recommendationRequestRcvDto)
        );

        assertEquals("Recommendation request must be sent once in 6 months", exception.getMessage());
    }

    @Test
    @DisplayName("SkillIdNotExist")
    void testCreateRecommendationRequest_SkillIdNotExist() {
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.empty());
        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(3L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(recommendationRequestRcvDto)
        );

        assertEquals("Skill with id = 3 not exist", exception.getMessage());
    }

    @Test
    @DisplayName("UserIdNotExist")
    void testCreateRecommendationRequest_UserIdNotExist() {
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.empty());
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(recommendationRequestRcvDto)
        );

        assertEquals("User with id 2 not found", exception.getMessage());
    }

    @Test
    @DisplayName("getRequest_Success")
    void getRequest_Success() {
        Long id = recommendationRequest.getId();
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));

        RecommendationRequestDto requestDto = recommendationRequestService.getRequest(id);

        assertNotNull(requestDto);
        assertEquals(recommendationRequest.getId(), requestDto.getId());
        assertEquals(recommendationRequest.getRequester().getId(), requestDto.getRequesterId());
        assertEquals(recommendationRequest.getReceiver().getId(), requestDto.getReceiverId());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestMapper, times(1)).toDto(recommendationRequest);
    }

    @Test
    @DisplayName("getRequest_NotFound")
    void getRequest_NotFound() {
        Long id = 5L;
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.getRequest(id));

        assertEquals("Recommendation request with id 5 not found", exception.getMessage());
        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("rejectRequest_Success")
    void rejectRequest_Success() {
        Long id = 1L;
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenAnswer(invocation -> {
                    RecommendationRequest savedRequest = invocation.getArgument(0);
                    savedRequest.setStatus(RequestStatus.REJECTED);
                    return savedRequest;
                });

        RecommendationRequestDto requestDto = recommendationRequestService.rejectRequest(id, rejectionDto);

        assertNotNull(requestDto);
        assertEquals(id, requestDto.getId());
        assertEquals(RequestStatus.REJECTED, requestDto.getStatus());
        assertEquals(rejectionDto.getReason(), requestDto.getRejectionReason());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
        verify(recommendationRequestMapper, times(1)).toDto(recommendationRequest);
    }

    @Test
    @DisplayName("rejectRequest_AlreadyRejected")
    void rejectRequest_AlreadyRejected() {
        Long id = 1L;
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.getReason());
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(id, rejectionDto));

        assertEquals("The recommendation request id 1 is already rejected", exception.getMessage());
        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("rejectRequest_NotFound")
    void rejectRequest_NotFound() {
        Long id = 5L;
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(id, rejectionDto));

        assertEquals("Recommendation request id 5 not found", exception.getMessage());
        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("getRequestsWithFilters_Successfully")
    void getRequestsWithFilters_Successfully() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .status(RequestStatus.PENDING)
                .build();

        RecommendationRequest request1 = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.ACCEPTED)
                .build();
        RecommendationRequest request2 = RecommendationRequest.builder()
                .id(2L)
                .status(RequestStatus.PENDING)
                .build();

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));

        List<RecommendationRequestDto> requests = recommendationRequestService.getRequests(requestFilterDto);

        assertEquals(1, requests.size());
        assertEquals(2L, requests.get(0).getId());
        assertEquals(RequestStatus.PENDING, requests.get(0).getStatus());
    }

    @Test
    @DisplayName("getRequestsWithFiltersByUser_Successfully")
    void getRequestsWithFiltersByUser_Successfully() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .requesterId(requester.getId())
                .receiverId(receiver.getId())
                .build();

        RecommendationRequest request1 = RecommendationRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.ACCEPTED)
                .build();
        RecommendationRequest request2 = RecommendationRequest.builder()
                .id(2L)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .build();

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));

        List<RecommendationRequestDto> requests = recommendationRequestService.getRequests(requestFilterDto);

        assertEquals(2, requests.size());
        assertEquals(1L, requests.get(0).getId());
        assertEquals(2L, requests.get(1).getId());
    }

    @Test
    @DisplayName("getRequestsWithFiltersByStatusAndUser_Successfully")
    void getRequestsWithFiltersByStatusAndUser_Successfully() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .status(RequestStatus.REJECTED)
                .requesterId(requester.getId())
                .receiverId(receiver.getId())
                .build();

        RecommendationRequest request1 = RecommendationRequest.builder()
                .id(1L)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.ACCEPTED)
                .build();
        RecommendationRequest request2 = RecommendationRequest.builder()
                .id(2L)
                .requester(requester)
                .receiver(receiver)
                .status(RequestStatus.PENDING)
                .build();

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));

        List<RecommendationRequestDto> requests = recommendationRequestService.getRequests(requestFilterDto);

        assertEquals(0, requests.size());
    }

    private User createUser(long id, String title) {
        return User.builder()
                .id(id)
                .username(title)
                .build();
    }

    private Skill createSkill(long id, String title) {
        return Skill.builder()
                .id(id)
                .title(title)
                .build();
    }

    private SkillRequest createSkillRequest(long id, RecommendationRequest recommendationRequest, Skill skill) {
        return SkillRequest.builder()
                .id(id)
                .request(recommendationRequest)
                .skill(skill)
                .build();
    }

    private RecommendationRequestRcvDto createRequestRcvDto(User requester,
                                                            User receiver,
                                                            RecommendationRequest request,
                                                            List<Long> skillIdsList) {
        return RecommendationRequestRcvDto.builder()
                .message(request.getMessage())
                .skillIds(skillIdsList)
                .requesterId(requester.getId())
                .receiverId(receiver.getId())
                .build();
    }
}


