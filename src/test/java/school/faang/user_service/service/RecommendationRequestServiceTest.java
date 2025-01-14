package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestSaveDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RecommendationRequestCreatedException;
import school.faang.user_service.exception.RequestAlreadyProcessedException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.UserService;
import school.faang.user_service.service.recommendation.impl.RecommendationRequestServiceImpl;
import school.faang.user_service.service.recommendation.impl.SkillRequestServiceImpl;
import school.faang.user_service.service.recommendation.util.RecommendationRequestFilter;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private SkillRequestServiceImpl skillRequestService;
    @Mock
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    @Mock
    private RecommendationRequestFilter recommendationRequestFilter;
    private RecommendationRequestServiceImpl recommendationRequestService;

    @BeforeEach
    void setUp() {
        recommendationRequestService = new RecommendationRequestServiceImpl(recommendationRequestRepository,
                userService, skillRequestService, recommendationRequestMapper, recommendationRequestFilter);
    }

    @Test
    void testCreateWithNotExistingRequester() {
        RecommendationRequestSaveDto  recommendationReqSaveDto = RecommendationReqDataFactory.createRecommendationRequestSaveDto();
        Mockito.when(userService.findById(recommendationReqSaveDto.requesterId()))
                .thenThrow(ResourceNotFoundException
                        .userNotFoundException(recommendationReqSaveDto.requesterId()));
        Assert.assertThrows(ResourceNotFoundException.class,
                () -> recommendationRequestService.create(recommendationReqSaveDto));
    }

    @Test
    void testCreateWithNotExistingReceiver() {
        RecommendationRequestSaveDto recommendationReqSaveDto = RecommendationReqDataFactory.createRecommendationRequestSaveDto();
        User requester = RecommendationReqDataFactory.createRequester();
        Mockito.when(userService.findById(recommendationReqSaveDto.requesterId()))
                .thenReturn(requester);
        Mockito.when(userService.findById(recommendationReqSaveDto.receiverId()))
                .thenThrow(ResourceNotFoundException
                        .userNotFoundException(recommendationReqSaveDto.receiverId()));
        Assert.assertThrows(ResourceNotFoundException.class,
                () -> recommendationRequestService.create(recommendationReqSaveDto));
    }

    @Test
    void testCreateWithSixMonthLimit() {
        RecommendationRequestSaveDto recommendationReqSaveDto = RecommendationReqDataFactory.createRecommendationRequestSaveDto();
        User requester = RecommendationReqDataFactory.createRequester();
        User receiver = RecommendationReqDataFactory.createReceiver();
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        Mockito.when(userService.findById(recommendationReqSaveDto.requesterId()))
                .thenReturn(requester);
        Mockito.when(userService.findById(recommendationReqSaveDto.receiverId()))
                .thenReturn(receiver);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(recommendationReqSaveDto.requesterId(),
                        recommendationReqSaveDto.receiverId()))
                .thenReturn(Optional.of(recommendationRequest));
        Assert.assertThrows(RecommendationRequestCreatedException.class,
                () -> recommendationRequestService.create(recommendationReqSaveDto));
    }

    @Test
    void testCreateSaveRecommendationRequest() {
        RecommendationRequestSaveDto saveDto = RecommendationReqDataFactory.createRecommendationRequestSaveDto();
        RecommendationRequestDto dto = RecommendationReqDataFactory.createRecommendationRequestDto();
        User requester = RecommendationReqDataFactory.createRequester();
        User receiver = RecommendationReqDataFactory.createReceiver();
        RecommendationRequest request = RecommendationReqDataFactory.createRecommendationRequest();
        RecommendationRequest savedRequest = RecommendationReqDataFactory.createRecommendationRequest();
        Mockito.when(userService.findById(1L))
                .thenReturn(requester);
        Mockito.when(userService.findById(2L))
                .thenReturn(receiver);
        Mockito.when(recommendationRequestMapper.toEntity(saveDto, requester, receiver))
                .thenReturn(request);
        Mockito.when(recommendationRequestRepository.save(request))
                .thenReturn(savedRequest);
        Mockito.when(recommendationRequestMapper.toDto(savedRequest))
                .thenReturn(dto);
        RecommendationRequestDto result = recommendationRequestService.create(saveDto);
        Assert.assertNotNull(result);
        Assert.assertEquals(1L, result.id());
        Mockito.verify(recommendationRequestRepository, times(1)).save(request);
    }

    @Test
    void getRequest_shouldReturnRequestIfExists() {
        RecommendationRequest request = RecommendationReqDataFactory.createRecommendationRequest();
        RecommendationRequestDto dto = RecommendationReqDataFactory.createRecommendationRequestDto();
        Mockito.when(recommendationRequestRepository.findById(1L))
                .thenReturn(Optional.of(request));
        Mockito.when(recommendationRequestMapper.toDto(request))
                .thenReturn(dto);
        RecommendationRequestDto result = recommendationRequestService.getRequest(1L);
        Assert.assertNotNull(result);
        Assert.assertEquals(1L, result.id());
    }

    @Test
    void getRequest_shouldThrowExceptionIfNotFound() {
        Mockito.when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.empty());
        Assert.assertThrows(ResourceNotFoundException.class,
                () -> recommendationRequestService.getRequest(1L));
    }

    @Test
    void rejectRequest_shouldSetStatusToRejected() {
        RecommendationRequest request = RecommendationReqDataFactory.createRecommendationRequest();
        RejectionDto rejectionDto = RecommendationReqDataFactory.createRejectionDto();
        Mockito.when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        recommendationRequestService.rejectRequest(1L, rejectionDto);
        Assert.assertEquals(RequestStatus.REJECTED, request.getStatus());
        Assert.assertEquals("Not suitable", request.getRejectionReason());
        Mockito.verify(recommendationRequestRepository, times(1)).findById(1L);
    }

    @Test
    void rejectRequest_shouldThrowExceptionIfAlreadyProcessed() {
        RecommendationRequest request = RecommendationReqDataFactory.createRecommendationRequest();
        request.setStatus(RequestStatus.ACCEPTED);
        Mockito.when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        Assert.assertThrows(RequestAlreadyProcessedException.class,
                () -> recommendationRequestService.rejectRequest(1L, new RejectionDto("Reason")));
    }
}
