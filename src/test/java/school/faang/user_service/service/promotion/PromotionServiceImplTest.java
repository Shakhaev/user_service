package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.mapper.promotion.PromotionMapperImpl;
import school.faang.user_service.repository.promotion.PromotionRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotion;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionResponseDto;

@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private PromotionPlanService promotionPlanService;

    @Mock
    private PromotionPaymentService promotionPaymentService;

    @Spy
    private PromotionMapperImpl promotionMapper;

    @InjectMocks
    private PromotionServiceImpl promotionService;

    @Test
    public void testGetPromotionsByUser() {
        when(promotionRepository.getPromotionByUserId(eq(1L)))
                .thenReturn(List.of(getPromotion()));

        List<PromotionResponseDto> promotionsByUserDto = promotionService.getPromotionsByUser(1L);

        assertEquals(List.of(getPromotionResponseDto()), promotionsByUserDto);
    }
}