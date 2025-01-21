package school.faang.user_service.service.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.entity.premium.PremiumPeriod;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void sendPaymentRequestSent() {
        PremiumPeriod period = PremiumPeriod.ONE_MONTH;

        paymentService.sendPaymentRequest(period);
        verify(paymentServiceClient, times(1)).sendPaymentRequest(any());
    }
}
