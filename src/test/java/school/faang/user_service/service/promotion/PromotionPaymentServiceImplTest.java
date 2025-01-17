package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.promotion.PromotionPaymentFeignClient;
import school.faang.user_service.dto.promotion.PromotionPaymentDto;
import school.faang.user_service.mapper.promotion.PromotionPaymentMapperImpl;
import school.faang.user_service.repository.promotion.PromotionPaymentRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionPayment;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionPaymentDto;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionRequestDto;

@ExtendWith(MockitoExtension.class)
class PromotionPaymentServiceImplTest {
    private static final String PROMOTION_PAYMENT_ID = "1";

    @Mock
    private PromotionPaymentRepository promotionPaymentRepository;

    @Mock
    private PromotionPaymentFeignClient client;

    @Spy
    private PromotionPaymentMapperImpl promotionPaymentMapper;

    @InjectMocks
    private PromotionPaymentServiceImpl service;

    @Test
    public void testSendAndCreatePromotionPayment() {
        when(promotionPaymentRepository.save(eq(getPromotionPayment()))).thenReturn(getPromotionPayment());
        when(client.sendPayment(any())).thenReturn(any());

        PromotionPaymentDto actualPromotionPaymentDto = service.sendAndCreate(getPromotionRequestDto());

        assertEquals(getPromotionPaymentDto(), actualPromotionPaymentDto);
    }


    @Test
    public void testGetPromotionPaymentByName() {
        when(promotionPaymentRepository.findPromotionPaymentById(eq(PROMOTION_PAYMENT_ID)))
                .thenReturn(getPromotionPayment());

        PromotionPaymentDto actualPromotionPaymentDto = service.getPromotionPaymentById(PROMOTION_PAYMENT_ID);

        assertEquals(getPromotionPaymentDto(), actualPromotionPaymentDto);
    }
}