package school.faang.user_service.service.recommendation;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    // Test for create()
    @Test
    void shouldCreateRecommendationRequestSuccessfully() {
        // Arrange
        RecommendationRequest request = RecommendationRequest.builder()
                .message("Recommendation message")
                .requester(User.builder()
                        .id(1L)
                        .username("Requester")
                        .email("requester@example.com")
                        .build())
                .receiver(User.builder()
                        .id(2L)
                        .username("Receiver")
                        .email("receiver@example.com")
                        .build())
                .skills(List.of(SkillRequest.builder()
                        .skill(Skill.builder()
                                .id(1L)
                                .title("Java")
                                .build())
                        .build()))
                .createdAt(LocalDateTime.now())
                .build();

        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L)).thenReturn(Optional.empty());
        when(recommendationRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RecommendationRequest result = recommendationRequestService.create(request);

        // Assert
        assertNotNull(result);
        verify(recommendationRequestRepository).save(recommendationRequestCaptor.capture());
        assertEquals("Recommendation message", recommendationRequestCaptor.getValue().getMessage());
        verify(skillRequestRepository).create(anyLong(), eq(1L));
    }

    @Test
    void shouldThrowExceptionWhenMessageIsEmpty() {
        // Arrange
        RecommendationRequest request = RecommendationRequest.builder()
                .message(" ")
                .build();

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(request));

        assertEquals("Message cannot be empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRequestSentWithinSixMonths() {
        // Arrange
        RecommendationRequest request = RecommendationRequest.builder()
                .message("Recommendation message")
                .requester(User.builder()
                        .id(1L)
                        .username("Requester")
                        .email("requester@example.com")
                        .build())
                .receiver(User.builder()
                        .id(2L)
                        .username("Receiver")
                        .email("receiver@example.com")
                        .build())
                .build();

        RecommendationRequest latestRequest = RecommendationRequest.builder()
                .createdAt(LocalDateTime.now().minusMonths(3))
                .build();

        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.of(latestRequest));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class,
                () -> recommendationRequestService.create(request));

        assertEquals("Cannot request recommendation more than once in 6 months", exception.getMessage());
    }

    @Test
    void shouldGetRequestsWithFilters() {
        // Arrange
        RequestFilterDto filter = new RequestFilterDto();
        filter.setStatus(RequestStatus.PENDING);

        RecommendationRequest request1 = RecommendationRequest.builder()
                .status(RequestStatus.PENDING)
                .build();
        RecommendationRequest request2 = RecommendationRequest.builder()
                .status(RequestStatus.ACCEPTED)
                .build();

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));

        // Act
        List<RecommendationRequest> result = recommendationRequestService.getRequests(filter);

        // Assert
        assertEquals(1, result.size());
        assertEquals(RequestStatus.PENDING, result.get(0).getStatus());
    }

    @Test
    void shouldGetRequestSuccessfully() {
        // Arrange
        RecommendationRequest request = RecommendationRequest.builder().build();
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        // Act
        RecommendationRequest result = recommendationRequestService.getRequest(1L);

        // Assert
        assertNotNull(result);
        verify(recommendationRequestRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenRequestNotFound() {
        // Arrange
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.getRequest(1L));

        assertEquals("Recommendation request not found", exception.getMessage());
    }

    @Test
    void shouldRejectRequestSuccessfully() {
        // Arrange
        RecommendationRequest request = RecommendationRequest.builder()
                .status(RequestStatus.PENDING)
                .build();

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(recommendationRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RecommendationRequest result = recommendationRequestService.rejectRequest(1L, "Not relevant");

        // Assert
        assertEquals(RequestStatus.REJECTED, result.getStatus());
        assertEquals("Not relevant", result.getRejectionReason());
        verify(recommendationRequestRepository).save(request);
    }

    @Test
    void shouldThrowExceptionWhenRejectingNonPendingRequest() {
        // Arrange
        RecommendationRequest request = RecommendationRequest.builder()
                .status(RequestStatus.ACCEPTED)
                .build();

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class,
                () -> recommendationRequestService.rejectRequest(1L, "Not relevant"));

        assertEquals("Cannot reject a non-pending request", exception.getMessage());
    }
}