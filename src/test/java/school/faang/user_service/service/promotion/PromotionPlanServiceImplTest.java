package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.promotion.PromotionPlanDto;
import school.faang.user_service.mapper.promotion.PromotionPlanMapperImpl;
import school.faang.user_service.repository.promotion.PromotionPlanRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionPlan;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionPlanDto;

@ExtendWith(MockitoExtension.class)
class PromotionPlanServiceImplTest {

    @Mock
    private PromotionPlanRepository promotionPlanRepository;

    @Spy
    private PromotionPlanMapperImpl promotionPlanMapper;

    @InjectMocks
    private PromotionPlanServiceImpl promotionPlanService;

    @Test
    public void testGetAllPromotionPlans() {
        when(promotionPlanRepository.findAll()).thenReturn(List.of(getPromotionPlan()));

        List<PromotionPlanDto> actualPromotionPlans = promotionPlanService.getPromotionPlans();

        assertEquals(List.of(getPromotionPlanDto()), actualPromotionPlans);
    }
}