package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.PromotionPlanRepository;
import school.faang.user_service.repository.promotion.PromotionRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotion;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionPlanPremium;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getUserDto;

@ExtendWith(MockitoExtension.class)
class PromotionSearchServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private PromotionPlanRepository promotionPlanRepository;

    @Mock
    private PromotionService promotionService;

    @Spy
    private UserMapperImpl userMapper;

    @Spy
    private EventMapperImpl eventMapper;

    @InjectMocks
    private PromotionSearchServiceImpl promotionSearchService;

    @Test
    public void testSearchResults() {
        when(promotionRepository.findAll()).thenReturn(List.of(getPromotion()));
        when(promotionPlanRepository.findPromotionPlanByName(eq("PREMIUM")))
                .thenReturn(getPromotionPlanPremium());
        doNothing().when(promotionService).updatePromotionViews(eq(1L));

        List<Object> result = promotionSearchService.searchResults("user", 5);

        assertEquals(result.get(0), getUserDto());
    }
}