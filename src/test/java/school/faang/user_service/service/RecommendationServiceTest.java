package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.skill_offer.CreateSkillOfferRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.mapper.recommendation.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Spy
    private RecommendationMapper recommendationMapper;
    @Spy
    private SkillOfferMapper skillOfferMapper;

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Test
    public void createRecommendation_ShouldThrowDataValidationExceptionWhenRecommendationContentIsEmpty() {
        List<CreateSkillOfferRequest> createSkillOfferRequests = List.of(new CreateSkillOfferRequest(), new CreateSkillOfferRequest());

        createSkillOfferRequests.get(0).setSkillId(1L);
        createSkillOfferRequests.get(1).setSkillId(2L);

        CreateRecommendationRequest createRecommendationRequest = new CreateRecommendationRequest();
        createRecommendationRequest.setAuthorId(1L);
        createRecommendationRequest.setReceiverId(2L);
        createRecommendationRequest.setContent("");
        createRecommendationRequest.setSkillOffers(createSkillOfferRequests);
        createRecommendationRequest.setCreatedAt(LocalDateTime.now());

        assertThrows(DataValidationException.class, () -> recommendationService.create(createRecommendationRequest));
    }

}
