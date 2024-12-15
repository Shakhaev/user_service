package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.message.event.RecommendationReceivedEvent;
import school.faang.user_service.message.producer.RecommendationReceivedEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.RecommendationValidator;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private UserService userService;

    @Mock
    private SkillService skillService;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private SkillOfferMapper skillOfferMapper;

    @Mock
    private RecommendationValidator recommendationValidator;

    @Mock
    private RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;


    @Test
    public void testPublishRecommendationReceivedEvent() {
        // arrange
        long recommenderUserId = 2L;
        long receiverId = 5L;
        long recommendationId = 8L;
        RecommendationReceivedEvent recommendationReceivedEvent = RecommendationReceivedEvent.builder()
                .recommenderUserId(recommenderUserId)
                .receiverId(receiverId)
                .recommendationId(recommendationId)
                .build();

        // act
        recommendationService.publishRecommendationReceivedEventAsync(
                recommenderUserId,
                receiverId,
                recommendationId
        );

        // assert
        verify(recommendationReceivedEventPublisher).publish(recommendationReceivedEvent);
    }
}
