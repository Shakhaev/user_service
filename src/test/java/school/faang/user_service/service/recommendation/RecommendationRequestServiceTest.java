package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Captor
    private ArgumentCaptor<RecommendationRequest> recommendationRequestCaptor;

    private RecommendationRequest recommendationRequest;
    private RecommendationRequest existingRequest;

    @BeforeEach
    void setUp() {
        User requester = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();

        Skill skill = Skill.builder().id(1L).title("Java").build();
        SkillRequest skillRequest = SkillRequest.builder().skill(skill).build();

        recommendationRequest = RecommendationRequest.builder()
                .message("Recommendation message")
                .requester(requester)
                .receiver(receiver)
                .skills(List.of(skillRequest))
                .createdAt(LocalDateTime.now())
                .build();

        existingRequest = RecommendationRequest.builder()
                .message("Existing recommendation")
                .requester(requester)
                .receiver(receiver)
                .createdAt(LocalDateTime.now().minusMonths(3))
                .build();
    }

    @Test
    void create_shouldSaveRecommendationRequest() {
        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(recommendationRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RecommendationRequest result = recommendationRequestService.create(recommendationRequest);

        assertNotNull(result);
        verify(recommendationRequestRepository).save(recommendationRequestCaptor.capture());
        RecommendationRequest capturedRequest = recommendationRequestCaptor.getValue();
        assertEquals("Recommendation message", capturedRequest.getMessage());
        verify(skillRequestRepository).saveAll(anyList());
    }

    @Test
    void create_shouldThrowExceptionWhenMessageIsEmpty() {
        recommendationRequest.setMessage(" ");
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(recommendationRequest));

        assertEquals("Message cannot be empty", exception.getMessage());
        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowExceptionWhenRequestSentWithinSixMonths() {
        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(existingRequest));

        Exception exception = assertThrows(IllegalStateException.class,
                () -> recommendationRequestService.create(recommendationRequest));

        assertEquals("Cannot request recommendation more than once in 6 months", exception.getMessage());
        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    void getRequests_shouldFilterRequestsBasedOnStatus() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setStatus(RequestStatus.PENDING);

        RecommendationRequest pendingRequest = RecommendationRequest.builder()
                .status(RequestStatus.PENDING).build();
        RecommendationRequest acceptedRequest = RecommendationRequest.builder()
                .status(RequestStatus.ACCEPTED).build();

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(pendingRequest, acceptedRequest));

        List<RecommendationRequest> result = recommendationRequestService.getRequests(filter);

        assertEquals(1, result.size());
        assertEquals(RequestStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void getRequest_shouldReturnRecommendationRequest() {
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));

        RecommendationRequest result = recommendationRequestService.getRequest(1L);

        assertNotNull(result);
        assertEquals(recommendationRequest, result);
        verify(recommendationRequestRepository).findById(1L);
    }

    @Test
    void getRequest_shouldThrowExceptionWhenNotFound() {
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.getRequest(1L));

        assertEquals("Recommendation request not found", exception.getMessage());
    }

    @Test
    void rejectRequest_shouldRejectPendingRequest() {
        recommendationRequest.setStatus(RequestStatus.PENDING);

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RecommendationRequest result = recommendationRequestService.rejectRequest(1L, "Not relevant");

        assertEquals(RequestStatus.REJECTED, result.getStatus());
        assertEquals("Not relevant", result.getRejectionReason());
        verify(recommendationRequestRepository).save(recommendationRequestCaptor.capture());
        assertEquals(RequestStatus.REJECTED, recommendationRequestCaptor.getValue().getStatus());
    }

    @Test
    void rejectRequest_shouldThrowExceptionForNonPendingRequest() {
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));

        Exception exception = assertThrows(IllegalStateException.class,
                () -> recommendationRequestService.rejectRequest(1L, "Not relevant"));

        assertEquals("Cannot reject a non-pending request", exception.getMessage());
        verify(recommendationRequestRepository, never()).save(any());
    }
}
