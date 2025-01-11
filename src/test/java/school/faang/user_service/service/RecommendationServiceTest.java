package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    private final static int TEST_PERIOD_TO_ADD_NEW_RECOMMENDATION = 3;

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);

    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private User author;

    @BeforeEach
    public void setUp() {
        author = new User();
        recommendationDto = RecommendationDto.builder()
                .id(1L)
                .authorId(2L)
                .receiverId(3L)
                .content("content")
                .skillOffers(List.of(SkillOfferDto.builder()
                        .id(1L)
                        .skillId(2L)
                        .build()))
                .build();

        recommendation = recommendationMapper.toEntity(recommendationDto);
    }

    @Test
    public void testCreateValidatePeriodToAddNewRecommendation() {
        author.setId(recommendationDto.getAuthorId());
        recommendation.setAuthor(author);
        recommendation.setCreatedAt(LocalDateTime.now().minusMonths(TEST_PERIOD_TO_ADD_NEW_RECOMMENDATION));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendation));

        assertThrows(BusinessException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testCreateValidateSkillInSystem() {


        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testCreateWithRecommendationDto() {
        when(userRepository.findById(recommendationDto.getAuthorId()))
                .thenReturn(Optional.ofNullable(User.builder().id(1L).build()));
        when(userRepository.findById(recommendationDto.getReceiverId()))
                .thenReturn(Optional.ofNullable(User.builder().id(2L).build()));
        when(skillOfferRepository.findAllByUserId(recommendationDto.getReceiverId()))
                .thenReturn(List.<SkillOffer>of(SkillOffer.builder()
                        .id(1L)
                        .skill(Skill.builder().id(1L).build())
                        .build()));

        recommendation.setAuthor(userRepository.findById(recommendationDto.getAuthorId()).orElseThrow());
        recommendation.setReceiver(userRepository.findById(recommendationDto.getReceiverId()).orElseThrow());
        recommendation.setSkillOffers(skillOfferRepository.findAllByUserId(recommendationDto.getReceiverId()));

        Recommendation result = recommendation;
        assertNotNull(result);
        assertEquals(1L, result.getAuthor().getId());
        assertEquals(2L, result.getReceiver().getId());
        assertEquals("content", result.getContent());
        assertEquals(1L, result.getSkillOffers().get(0).getSkill().getId());
    }

    @Test
    public void testCreateWithSaveSkillOffers() {

    }

    @Test
    public void testGetAllGivenRecommendations() {
        when(recommendationRepository.findAllByAuthorId(recommendationDto.getAuthorId(),
                Pageable.unpaged()))
                .thenReturn((Page<Recommendation>) recommendationDto);

        recommendationService.getAllGivenRecommendations(recommendation.getAuthor().getId());

        verify(recommendationRepository).findAllByAuthorId(recommendationDto.getAuthorId(), Pageable.unpaged());
        verify(recommendationMapper).mapToDtoList((List<Recommendation>) recommendation);

    }

    @Test
    void testDeleteWithValidateExistsById() {
        recommendation.setId(10L);
        when(recommendationRepository.existsById(recommendation.getId())).thenReturn(false);

        Exception result = assertThrows(DataValidationException.class, () ->
                recommendationService.delete(recommendation.getId()));

        assertEquals("Рекомендация с id:" + recommendation.getId() + " не найдена в системе",
                result.getMessage());
    }

    @Test
    void testDeleteSuccessful() {
        recommendation.setId(10L);
        when(recommendationRepository.existsById(recommendation.getId())).thenReturn(true);

        recommendationService.delete(recommendation.getId());

        verify(recommendationRepository, times(1)).deleteById(recommendation.getId());
    }


//    private List<Long> skillByIds(RecommendationDto dto) {
//        return dto.getSkillOffers().stream()
//                .map(SkillOfferDto::getSkillId)
//                .toList();
//    }
//
//    private RecommendationDto prepareData(String correctContent) {
//        RecommendationDto dto = new RecommendationDto();
//        dto.setContent(correctContent);
//        return dto;
//    }
}
