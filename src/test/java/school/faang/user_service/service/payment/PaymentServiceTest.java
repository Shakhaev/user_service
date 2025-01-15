package school.faang.user_service.service.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.payment.PaymentServiceClient;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.utility.validator.impl.payment.PaymentRequestValidator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PaymentRequestValidator validator;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void sendPaymentRequestReturnSuccessResponse() {
        PremiumPeriod period = PremiumPeriod.ONE_MONTH;

        doNothing().when(validator).validate(any());
        paymentService.sendPaymentRequest(period);
        verify(paymentServiceClient, times(1)).sendPaymentRequest(any());
    }
}
