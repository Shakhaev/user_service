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
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.user.UserSkillGuaranteeDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreate() {
        // Подготовка данных
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setContent("Test recommendation");

        UserSkillGuaranteeDto userSkillGuaranteeDto = new UserSkillGuaranteeDto();
        userSkillGuaranteeDto.setSkillId(3L);
        userSkillGuaranteeDto.setGuarantorId(recommendationDto.getAuthorId());
        userSkillGuaranteeDto.setUserId(recommendationDto.getReceiverId());

        // Настройка поведения моков
        when(skillOffer.getSkill().getId()).thenReturn(3L);
        when(userSkillGuaranteeMapper.toEntity(userSkillGuaranteeDto)).thenReturn(new UserSkillGuarantee());
        when(recommendationRepository.create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent())).thenReturn(1L);

        // Вызов тестируемого метода
        RecommendationDto result = recommendationService.create(recommendationDto);

        // Проверка результатов
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userSkillGuaranteeRepository).save(any(UserSkillGuarantee.class));
        verify(recommendationRepository).create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
    }

    @Test
    public void testDeleteRecommendation() {
        recommendationService.delete(1L);

        verify(recommendationRepository, times(1)).deleteById(1L);
    }
}