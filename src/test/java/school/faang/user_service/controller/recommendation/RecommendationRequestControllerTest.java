package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.faang.user_service.BaseTest;
import school.faang.user_service.data.RecommendationRequestData;
import school.faang.user_service.data.SkillData;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest extends BaseTest {
    @Autowired
    private RecommendationRequestController recommendationRequestController;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private SkillRepository skillRepository;
    @MockBean
    private RecommendationRequestRepository recommendationRequestRepository;
    @MockBean
    private SkillRequestRepository skillRequestRepository;

    @BeforeEach
    public void setUp() {
        Mockito.reset(userRepository);
        Mockito.reset(skillRepository);
        Mockito.reset(recommendationRequestRepository);
        Mockito.reset(skillRequestRepository);
    }

    @Test
    public void requestRecommendationSuccess() {
        RecommendationRequestData data = RecommendationRequestData.DATA1;

        mockData(data);

        RecommendationRequestDto response = recommendationRequestController.requestRecommendation(data.toDto());
        Assertions.assertNotNull(response);

        Assertions.assertEquals(data.getMessage(), response.getMessage());
    }

    @Test
    public void requestRecommendationLessMinMonthFail() {
        RecommendationRequestData data = RecommendationRequestData.DATA1;
        mockData(data);

        recommendationRequestController.requestRecommendation(data.toDto());

        try {
            mockData(data);
            mockFindLatestPendingRequest(Optional.of(data.toRecommendationRequest()));
            recommendationRequestController.requestRecommendation(data.toDto());
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Less than min months have passed since the previous request", e.getMessage());
        }
    }

    @Test
    public void requestRecommendationNotFoundUserFail() {
        RecommendationRequestData data = RecommendationRequestData.DATA1;

        when(userRepository.findAllById(any())).thenReturn(List.of());
        try {
            recommendationRequestController.requestRecommendation(data.toDto());
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Requester or receiver not found", e.getMessage());
        }
    }

    @Test
    public void requestRecommendationNullMessageFail() {
        RecommendationRequestData data = RecommendationRequestData.DATA_NULL_MESSAGE;

        try {
            recommendationRequestController.requestRecommendation(data.toDto());
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("message must not be null", e.getMessage());
        }
    }

    @Test
    void getRecommendationRequests() {
        RecommendationRequestData data = RecommendationRequestData.DATA1;
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(data.toRecommendationRequest()));
        List<RecommendationRequest> response = recommendationRequestController.getRecommendationRequests(data.toFilterDto());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void getRecommendationRequest() {
        RecommendationRequestData data = RecommendationRequestData.DATA1;

        when(recommendationRequestRepository.findById(any())).thenReturn(Optional.of(data.toRecommendationRequest()));
        RecommendationRequest request = recommendationRequestController.getRecommendationRequest(data.getId());
        Assertions.assertNotNull(request);
        Assertions.assertEquals(data.getId(), request.getId());
    }

    private void mockData(RecommendationRequestData data) {
        mockUserData(data.getRequester().getUser());
        mockFindLatestPendingRequest(Optional.empty());
        mockSkillsFindById(data);
        when(recommendationRequestRepository.save(any())).thenReturn(data.toRecommendationRequest());
        when(skillRequestRepository.saveAll(any())).thenReturn(data.getSkillsRequested().stream().map(skillData ->
                SkillRequest.builder()
                        .skill(skillData.toSkill())
                        .request(data.toRecommendationRequest())
                        .id(1)
                        .build()
                ).toList()
        );
    }

    private void mockSkillsFindById(RecommendationRequestData data) {
        when(skillRepository.findAllById(any())).thenReturn(data.getSkillsRequested().stream().map(SkillData::toSkill).toList());
        when(skillRepository.findById(any())).thenReturn(Optional.of(data.getSkillsRequested().get(0).toSkill()));
    }

    private void mockFindLatestPendingRequest(Optional<RecommendationRequest> returnValue) {
        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong())).thenReturn(returnValue);
    }

    private void mockUserData(User user) {
        when(userRepository.findAllById(any())).thenReturn(List.of(user));
    }
}