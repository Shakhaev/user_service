package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.RecommendationController;
import school.faang.user_service.dto.recommendation.RecommendationDto;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {

    @InjectMocks
    private RecommendationController recommendationController;

    @Mock
    private RecommendationService recommendationService;

    private RecommendationDto recommendationDtoTestNull;
    private RecommendationDto recommendationDtoTestEmpty;
    private RecommendationDto recommendationDtoTestIsCreate;

    @BeforeEach
    void setUp(){
        recommendationDtoTestNull = new RecommendationDto(1L, 2L, 3L, null, null, null);
        recommendationDtoTestEmpty = new RecommendationDto(1L, 2L, 3L, "  ", null, null);
        recommendationDtoTestIsCreate = new RecommendationDto(1L, 2L, 3L, "content", null, null);
    }

    @Test
    public void testNullContentIsInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> recommendationController.giveRecommendation(recommendationDtoTestNull));
    }

    @Test
    public void testEmptyContentIsInvalid() {
        assertThrows(IllegalArgumentException.class,
                () -> recommendationController.giveRecommendation(recommendationDtoTestEmpty));
    }

    @Test
    public void testRecommendationIsCreate() {
        recommendationController.giveRecommendation(recommendationDtoTestIsCreate);

        Mockito.verify(recommendationService, Mockito.times(1))
                .create(recommendationDtoTestIsCreate);
    }
}
