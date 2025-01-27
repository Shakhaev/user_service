package school.faang.user_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.user.UserSkillGuaranteeDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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
    private SkillOffer skillOffer;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private UserSkillGuaranteeMapper userSkillGuaranteeMapper;

    @Mock
    private RecommendationMapper recommendationMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateRecommendation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setContent("Great work!");

        when(skillOffer.getSkill().getId()).thenReturn(1L);
        when(recommendationRepository.create(anyLong(), anyLong(), anyString())).thenReturn(1L);
        when(userSkillGuaranteeMapper.toEntity(any(UserSkillGuaranteeDto.class))).thenReturn(new UserSkillGuarantee());

        RecommendationDto result = recommendationService.create(recommendationDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(recommendationRepository, times(1)).create(anyLong(), anyLong(), anyString());
        verify(userSkillGuaranteeRepository, times(1)).save(any(UserSkillGuarantee.class));
    }

    @Test
    public void testUpdateRecommendation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setContent("Updated content");
        recommendationDto.setSkillOffers(Collections.emptyList());

        doNothing().when(skillOfferRepository).deleteAllByRecommendationId(anyLong());
        when(skillOfferRepository.create(anyLong(), anyLong())).thenReturn(1L);

        RecommendationDto result = recommendationService.update(recommendationDto);

        assertNotNull(result);
        assertEquals(recommendationDto.getId(), result.getId());
        verify(skillOfferRepository, times(1)).deleteAllByRecommendationId(anyLong());
        verify(skillOfferRepository, times(0)).create(anyLong(), anyLong());
    }

    @Test
    public void testDeleteRecommendation() {
        recommendationService.delete(1L);

        verify(recommendationRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetAllUserRecommendations() {
        when(recommendationRepository.findAllByReceiverId(anyLong(), any())).thenReturn(Page.empty());

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recommendationRepository, times(1)).findAllByReceiverId(anyLong(), any());
    }

    @Test
    public void testGetAllGivenRecommendations() {
        when(recommendationRepository.findAllByAuthorId(anyLong())).thenReturn(Page.empty());

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recommendationRepository, times(1)).findAllByAuthorId(anyLong());
    }

    @Test
    public void testValidateMonthsBetweenRecommendations() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);

        Recommendation recommendation = new Recommendation();
        recommendation.setCreatedAt(LocalDateTime.now().minusMonths(7));

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendation));

        assertDoesNotThrow(() -> recommendationService.validateMonthsBetweenRecommendations(recommendationDto));
    }

    @Test
    public void testValidateSkillOffers() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setSkillOffers(Collections.emptyList());

        assertDoesNotThrow(() -> recommendationService.create(recommendationDto));
    }}