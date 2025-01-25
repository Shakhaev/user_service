package school.faang.user_service.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillOfferMapper skillOfferMapper;

    @Test
    public void testCreateWithBlankContent(){
       //when(recommendationService.create(any())).thenThrow(new DataValidationException("Content cannot be blank"));
       assertThrows(DataValidationException.class, () -> recommendationService.create(new RecommendationDto(1L, 2L, 3L, "", null, null)));
    }
}