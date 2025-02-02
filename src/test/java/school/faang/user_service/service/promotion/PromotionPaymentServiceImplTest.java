package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.promotion.PromotionPaymentFeignClient;
import school.faang.user_service.dto.promotion.PaymentResponse;
import school.faang.user_service.dto.promotion.PromotionPaymentDto;
import school.faang.user_service.enums.promotion.Currency;
import school.faang.user_service.enums.promotion.PaymentStatus;
import school.faang.user_service.mapper.promotion.PromotionPaymentMapperImpl;
import school.faang.user_service.repository.promotion.PromotionPaymentRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionPayment;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionPaymentDto;
import static school.faang.user_service.utils.promotion.PromotionPrepareData.getPromotionRequestDto;

@ExtendWith(MockitoExtension.class)
class PromotionPaymentServiceImplTest {
    private static final UUID PROMOTION_PAYMENT_ID = UUID.randomUUID();

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
        UUID paymentNumber = UUID.randomUUID();
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .status(PaymentStatus.SUCCESS)
                .verificationCode(13123)
                .paymentNumber(paymentNumber)
                .amount(new BigDecimal(100))
                .currency(Currency.EUR)
                .message("test")
                .build();
        when(client.sendPayment(any())).thenReturn(paymentResponse);
        when(promotionPaymentRepository.save(any())).thenReturn(getPromotionPayment());

        PromotionPaymentDto actualPromotionPaymentDto = service.sendAndCreate(getPromotionRequestDto());

        assertEquals(getPromotionPaymentDto(), actualPromotionPaymentDto);
    }


    @Test
    public void testGetPromotionPaymentByName() {
        when(promotionPaymentRepository.findPromotionPaymentById(eq(PROMOTION_PAYMENT_ID)))
                .thenReturn(Optional.ofNullable(getPromotionPayment()));

        PromotionPaymentDto actualPromotionPaymentDto = service.getPromotionPaymentById(PROMOTION_PAYMENT_ID);

        assertEquals(getPromotionPaymentDto(), actualPromotionPaymentDto);
    }
}