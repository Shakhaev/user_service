package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestValidatorTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private RecommendationRequestValidator validator;

    @Test
    void testCheckUsersExist_ShouldThrowExceptionWhenRequesterDoesNotExist() {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.checkUsersExist(5L, 1L));
        assertEquals("Requester not found", exception.getMessage());
    }

    @Test
    void testCheckUsersExist_ShouldThrowExceptionWhenReceiverDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.checkUsersExist(1L, 5L));
        assertEquals("Receiver not found", exception.getMessage());
    }

    @Test
    void testCheckUsersExist_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        assertDoesNotThrow(() -> validator.checkUsersExist(1L, 2L));
    }

    @Test
    void testCheckRequestWithinSixMonthsExist_ShouldThrowExceptionWhenRequestExists() {
        when(recommendationRequestRepository.existsRequestWithinSixMonths(1L, 2L))
                .thenReturn(true);
        assertThrows(DataValidationException.class, () ->
                validator.checkRequestWithinSixMonthsExist(1L, 2L));
    }

    @Test
    void testCheckRequestWithinSixMonthsExist_Success() {
        when(recommendationRequestRepository.existsRequestWithinSixMonths(3L, 4L))
                .thenReturn(false);
        assertDoesNotThrow(() -> validator.checkRequestWithinSixMonthsExist(3L, 4L));
    }

    @Test
    void testCheckAllSkillsExist_ShouldThrowExceptionWhenNotAllSkillsExist() {
        when(skillRepository.existsById(1L)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> validator.checkAllSkillsExist(List.of(1L, 2L, 3L)));
    }

    @Test
    void testCheckAllSkillsExist_Success() {
        when(skillRepository.existsById(anyLong())).thenReturn(true);
        assertDoesNotThrow(() -> validator.checkAllSkillsExist(List.of(1L, 2L, 3L)));
    }

    @Test
    void testValidateRecommendationRequestStatus_ShouldThrowExceptionWhenStatusIsAccepted() {
        RecommendationRequest request = RecommendationRequest.builder().status(RequestStatus.ACCEPTED).build();
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateRecommendationRequestStatus(request));
        assertEquals("Recommendation request is already accepted", exception.getMessage());

    }

    @Test
    void testValidateRecommendationRequestStatus_ShouldThrowExceptionWhenStatusIsRejected() {
        RecommendationRequest request = RecommendationRequest.builder().status(RequestStatus.REJECTED).build();
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateRecommendationRequestStatus(request));
        assertEquals("Recommendation request is already rejected", exception.getMessage());
    }

    @Test
    void testValidateRecommendationRequestStatus_Success() {
        RecommendationRequest request = RecommendationRequest.builder().status(RequestStatus.PENDING).build();
        assertDoesNotThrow(() -> validator.validateRecommendationRequestStatus(request));
    }

    @Test
    void testCheckRecommendationRequestExists_ShouldThrowExceptionWhenRequestDoesNotExist() {
        Optional<RecommendationRequest> request = Optional.empty();
        assertThrows(DataValidationException.class, () -> validator.checkRecommendationRequestExists(request));
    }

    @Test
    void testCheckRecommendationRequestExists_Success() {
        Optional<RecommendationRequest> request = Optional.of(new RecommendationRequest());
        assertDoesNotThrow(() -> validator.checkRecommendationRequestExists(request));
    }
}