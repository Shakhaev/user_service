package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
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
import school.faang.user_service.service.recommendation.RecommendationRequestServiceImpl;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestFilter;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestReceiverFilter;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestRequesterFilter;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestStatusFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.TestData.createFilterDto;
import static school.faang.user_service.service.TestData.createRejectDto;
import static school.faang.user_service.service.TestData.createRequest;
import static school.faang.user_service.service.TestData.createRequestRcvDto;
import static school.faang.user_service.service.TestData.createSkill;
import static school.faang.user_service.service.TestData.createSkillRequest;
import static school.faang.user_service.service.TestData.createUser;

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
    private List<RecommendationRequestFilter> filters;

    @BeforeEach
    void setUp() {
        filters = new ArrayList<>(List.of(
                new RecommendationRequestStatusFilter(),
                new RecommendationRequestRequesterFilter(),
                new RecommendationRequestReceiverFilter()));

        recommendationRequestService = new RecommendationRequestServiceImpl(
                recommendationRequestRepository,
                recommendationRequestMapper,
                userRepository,
                skillRepository,
                skillRequestRepository,
                filters);

        requester = createUser(1L, "Requester");
        receiver = createUser(2L, "Receiver");

        skill1 = createSkill(1L, "Java");
        skill2 = createSkill(2L, "Kotlin");
        skill3 = createSkill(3L, "Hibernate");

        recommendationRequest = createRequest(1L, requester, receiver, RequestStatus.PENDING);

        skillRequest1 = createSkillRequest(1L, recommendationRequest, skill1);
        skillRequest2 = createSkillRequest(2L, recommendationRequest, skill2);
        skillRequest3 = createSkillRequest(3L, recommendationRequest, skill3);

        recommendationRequestRcvDto = createRequestRcvDto(requester, receiver, recommendationRequest,
                Arrays.asList(1L, 2L, 3L));

        rejectionDto = createRejectDto("Can't confirm.");
    }

    @Test
    void testCreateRecommendationRequest_Successfully() {
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

        RecommendationRequestDto requestFromDB = recommendationRequestService.createRequest(recommendationRequestRcvDto);

        verifyNoMoreInteractions(userRepository, recommendationRequestRepository, skillRepository);
        verify(recommendationRequestRepository, Mockito.times(1))
                .save(recommendationRequestCaptor.capture());
        assertEquals(recommendationRequestRcvDto.message(), recommendationRequestCaptor.getValue().getMessage());

        assertNotNull(requestFromDB);
        assertEquals(1L, requestFromDB.id());
        assertEquals(recommendationRequestRcvDto.requesterId(), requestFromDB.requesterId());
        assertEquals(recommendationRequestRcvDto.receiverId(), requestFromDB.receiverId());
        assertEquals(recommendationRequestRcvDto.message(), requestFromDB.message());
        assertEquals(RequestStatus.PENDING, requestFromDB.status());
        assertEquals(recommendationRequestRcvDto.skillIds(), requestFromDB.skillIds());
    }

    @Test
    void testCreateRecommendationRequest_UserRequestHimself() {
        RecommendationRequestRcvDto requestDto = createRequestRcvDto(requester, requester, recommendationRequest,
                Arrays.asList(1L, 2L, 3L));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.createRequest(requestDto));

        assertEquals("The user with id 1 cannot send a request to himself", exception.getMessage());
    }

    @Test
    void testCreateRecommendationRequest_RequestPeriodIsNotExceeded() {
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.of(RecommendationRequest.builder()
                        .createdAt(LocalDateTime.now().minusMonths(3))
                        .build()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.createRequest(recommendationRequestRcvDto)
        );

        assertEquals("Recommendation request must be sent once in 6 months,"
                + " the previous request with id = 0 was no more than 6 months ago", exception.getMessage());
    }

    @Test
    void testCreateRecommendationRequest_SkillIdNotExist() {
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.empty());
        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(3L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.createRequest(recommendationRequestRcvDto)
        );

        assertEquals("Skill with id = 3 not exist", exception.getMessage());
    }

    @Test
    void testCreateRecommendationRequest_UserIdNotExist() {
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.empty());
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.createRequest(recommendationRequestRcvDto)
        );

        assertEquals("User with id 2 not found", exception.getMessage());
    }

    @Test
    void testGetRequestById_Successfully() {
        Long id = recommendationRequest.getId();
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));

        RecommendationRequestDto requestDto = recommendationRequestService.getRequest(id);

        assertNotNull(requestDto);
        assertEquals(recommendationRequest.getId(), requestDto.id());
        assertEquals(recommendationRequest.getRequester().getId(), requestDto.requesterId());
        assertEquals(recommendationRequest.getReceiver().getId(), requestDto.receiverId());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestMapper, times(1)).toRecommendationRequestDto(recommendationRequest);
    }

    @Test
    void testGetRequestById_NotFoundRequest() {
        Long id = 5L;
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.getRequest(id));

        assertEquals("Recommendation request with id 5 not found", exception.getMessage());
        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestMapper, never()).toRecommendationRequestDto(any());
    }

    @Test
    void testRejectRequest_Successfully() {
        Long id = recommendationRequest.getId();
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenAnswer(invocation -> {
                    RecommendationRequest savedRequest = invocation.getArgument(0);
                    savedRequest.setStatus(RequestStatus.REJECTED);
                    return savedRequest;
                });

        RecommendationRequestDto requestDto = recommendationRequestService.rejectRequest(id, rejectionDto);

        assertNotNull(requestDto);
        assertEquals(id, requestDto.id());
        assertEquals(RequestStatus.REJECTED, requestDto.status());
        assertEquals(rejectionDto.reason(), requestDto.rejectionReason());

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
        verify(recommendationRequestMapper, times(1)).toRecommendationRequestDto(recommendationRequest);
    }

    @Test
    void testRejectRequest_AlreadyRejectedRequest() {
        Long id = recommendationRequest.getId();
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.reason());
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(id, rejectionDto));

        assertEquals("The recommendation request id 1 is already rejected", exception.getMessage());
        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toRecommendationRequestDto(any());
    }

    @Test
    void testRejectRequest_NotFoundRequest() {
        Long id = 5L;
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.rejectRequest(id, rejectionDto));

        assertEquals("Recommendation request id 5 not found", exception.getMessage());
        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, never()).save(any());
        verify(recommendationRequestMapper, never()).toRecommendationRequestDto(any());
    }

    @Test
    void testGetRequestsWithFiltersByStatusAndRequesterAndReceiver_Successfully() {
        RecommendationRequest request1 = createRequest(1L, requester, receiver, RequestStatus.ACCEPTED);
        RecommendationRequest request2 = createRequest(2L, requester, receiver, RequestStatus.PENDING);
        RecommendationRequest request3 = createRequest(3L, requester, receiver, RequestStatus.REJECTED);
        RequestFilterDto requestFilterDto = createFilterDto(request2.getStatus(),
                request2.getRequester().getId(), request2.getReceiver().getId());

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2, request3));

        List<RecommendationRequestDto> requests = recommendationRequestService.getRequests(requestFilterDto);

        assertEquals(1, requests.size());
        assertEquals(request2.getId(), requests.get(0).id());
        assertEquals(request2.getStatus(), requests.get(0).status());
        assertEquals(request2.getRequester().getId(), requests.get(0).requesterId());
        assertEquals(request2.getReceiver().getId(), requests.get(0).receiverId());
    }

    @Test
    void testGetRequestsWithFiltersByUser_Successfully() {
        RecommendationRequest request1 = createRequest(1L, requester, receiver, RequestStatus.ACCEPTED);
        RecommendationRequest request2 = createRequest(2L, requester, receiver, RequestStatus.PENDING);
        RecommendationRequest request3 = createRequest(3L, receiver, requester, RequestStatus.REJECTED);
        RequestFilterDto requestFilterDto = createFilterDto(null, requester.getId(), null);

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2, request3));

        List<RecommendationRequestDto> requests = recommendationRequestService.getRequests(requestFilterDto);

        assertEquals(2, requests.size());
        assertEquals(request1.getId(), requests.get(0).id());
        assertEquals(request2.getId(), requests.get(1).id());
    }

    @Test
    void testGetRequestsWithFiltersByStatus_NotFoundRequest() {
        RecommendationRequest request1 = createRequest(1L, requester, receiver, RequestStatus.ACCEPTED);
        RecommendationRequest request2 = createRequest(2L, requester, receiver, RequestStatus.PENDING);
        RecommendationRequest request3 = createRequest(3L, receiver, requester, RequestStatus.PENDING);
        RequestFilterDto requestFilterDto = createFilterDto(RequestStatus.REJECTED, null, null);

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2, request3));
        assertEquals(List.of(), recommendationRequestService.getRequests(requestFilterDto));
    }
}


