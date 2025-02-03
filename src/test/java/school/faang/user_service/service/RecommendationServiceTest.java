package school.faang.user_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.user.UserSkillGuaranteeDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.mapper.UserSkillGuaranteeMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    SkillRepository skillRepository;
    @Mock
    UserSkillGuaranteeRepository guaranteeRepository;
    @Spy
    private RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);

    @Test
    public void testCreateRecommendationWithOfferInValid() {

        assertThrows(DataValidationException.class,
                () -> recommendationService.create(getRecommendationDtoWithOffer()));

        Mockito.verify(recommendationRepository, Mockito.times(0))
                .create(12l, 14l, "content");
    }

    private Recommendation getRecommendationInvalidCreatedDate() {
        return new Recommendation(5L, "old content", getAuthorUser(), getReceiverUser(),
                null, null, LocalDateTime.now().minusMonths(2), null);
    }

    private RecommendationDto getRecommendationDtoWithNullableOffer() {
        return RecommendationDto.builder()
                .content("content")
                .authorId(12L)
                .receiverId(14L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private RecommendationDto getRecommendationDtoWithOffer() {
        RecommendationDto recommendationDto = getRecommendationDtoWithNullableOffer();
        recommendationDto.setSkillOffers(skillOffersDto());
        return recommendationDto;
    }
    private List<SkillOfferDto> skillOffersDto() {
        return List.of(SkillOfferDto.builder().id(18L).skillId(15L).build());
    }

    private User getAuthorUser() {
        return User.builder().id(12L).build();
    }

    private User getReceiverUser() {
        return User.builder().id(14L).build();
    }

    @Test
    public void testDelete() {
        Mockito.doNothing().when(recommendationRepository).deleteById(1L);

        recommendationService.delete(1L);

        verify(recommendationRepository).deleteById(1L);
    }
}




