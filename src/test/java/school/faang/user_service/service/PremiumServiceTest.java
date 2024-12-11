package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.client.payment.Currency;
import school.faang.user_service.client.payment.PaymentResponse;
import school.faang.user_service.client.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PaymentValidator;
import school.faang.user_service.validator.PremiumValidator;
import school.faang.user_service.validator.UserValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @Mock
    private PremiumRepository premiumRepository;

    @Mock
    private PremiumValidator premiumValidator;

    @Mock
    private UserService userService;

    @Mock
    private PaymentValidator paymentValidator;

    @Spy
    private PremiumMapper premiumMapper;

    @Mock
    private PaymentService paymentService;

    @Mock
    UserValidator userValidator;

    @Mock
    private PremiumCleanerService premiumCleanerService;

    @InjectMocks
    private PremiumService premiumService;

    @Captor
    private ArgumentCaptor<List<Premium>> captor;

    private Premium premium1;
    private Premium premium2;
    private Premium premium3;
    private LocalDateTime time;
    private final long userId = 1L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(premiumService, "batchSize", 2);
        time = LocalDateTime.now();
        premium1 = mock(Premium.class);
        premium2 = mock(Premium.class);
        premium3 = mock(Premium.class);
    }

    @Test
    void testBuyPremiumSuccessful() {
        when(userService.findUserById(userId)).thenReturn(new User());
        when(paymentService.sentPayment(any(PremiumPeriod.class)))
                .thenReturn(new PaymentResponse(PaymentStatus.SUCCESS, 1234, 123456789L,
                        BigDecimal.valueOf(9.99), Currency.USD, "Payment Successful"));
        when(premiumMapper.toDto(any(Premium.class))).thenReturn(new PremiumDto());
        when(premiumRepository.save(any(Premium.class))).thenReturn(setUpPremium());

        PremiumDto result = premiumService.buyPremium(userId, setUpPeriodMonth());

        assertNotNull(result);
        verify(premiumValidator).validateUserIsNotPremium(userId);
        verify(paymentService).sentPayment(any(PremiumPeriod.class));
        verify(paymentValidator).checkIfPaymentSuccess(any(PaymentResponse.class));
        verify(premiumRepository).save(any(Premium.class));
        verify(premiumMapper).toDto(any(Premium.class));
    }

    @Test
    void testBuyPremiumUserAlreadyPremium() {
        doThrow(new IllegalArgumentException("User with userId: " + userId + " already has premium"))
                .when(premiumValidator).validateUserIsNotPremium(userId);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                premiumService.buyPremium(userId, setUpPeriodMonth()));

        assertEquals("User with userId: " + userId + " already has premium", exception.getMessage());
        verify(premiumValidator).validateUserIsNotPremium(userId);
        verifyNoInteractions(paymentService, paymentValidator, premiumRepository, premiumMapper);
    }

    @Test
    void testBuyPremiumPaymentFailed() {
        PaymentResponse failedResponse = new PaymentResponse(PaymentStatus.FAILED,
                1234, 123456789L, BigDecimal.valueOf(9.99),
                Currency.USD, "Payment Failed");

        when(paymentService.sentPayment(any())).thenReturn(failedResponse);

        doThrow(new PaymentFailedException("Payment status:" + failedResponse.message()))
                .when(paymentValidator).checkIfPaymentSuccess(failedResponse);

        Exception exception = assertThrows(PaymentFailedException.class, () ->
                premiumService.buyPremium(userId, setUpPeriodMonth()));

        assertEquals("Payment status:" + failedResponse.message(), exception.getMessage());
        verify(premiumValidator).validateUserIsNotPremium(userId);
        verify(paymentService).sentPayment(any(PremiumPeriod.class));
        verify(paymentValidator).checkIfPaymentSuccess(failedResponse);
        verifyNoInteractions(premiumRepository, premiumMapper);
    }

    private PremiumPeriod setUpPeriodMonth() {
        return PremiumPeriod.MONTH;
    }

    private Premium setUpPremium() {
        return Premium.builder()
                .user(new User())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();
    }

    @Test
    void testDeleteAllPremium_successfulDeletion() {
        when(premiumRepository.findAllByEndDateBefore(time))
                .thenReturn(Arrays.asList(premium1, premium2, premium3));

        premiumService.deleteExpiredPremiums(time);

        verify(premiumRepository, times(1)).findAllByEndDateBefore(time);
        verify(premiumCleanerService, times(2)).deletePremium(captor.capture());
        List<List<Premium>> capturedBatches = captor.getAllValues();

        assertEquals(2, capturedBatches.size());
        assertEquals(Arrays.asList(premium1, premium2), capturedBatches.get(0));
        assertEquals(Collections.singletonList(premium3), capturedBatches.get(1));
    }

    @Test
    void deleteAllPremiumWithEmptyListShouldNotCallDelete() {
        LocalDateTime mockTime = LocalDateTime.now();
        when(premiumRepository.findAllByEndDateBefore(mockTime)).thenReturn(List.of());

        premiumService.deleteExpiredPremiums(mockTime);

        verify(premiumCleanerService, never()).deletePremium(anyList());
    }
}
