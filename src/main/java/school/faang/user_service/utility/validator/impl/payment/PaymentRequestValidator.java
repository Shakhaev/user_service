package school.faang.user_service.utility.validator.impl.payment;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.payment_service.Currency;
import school.faang.user_service.dto.payment_service.PaymentRequest;
import school.faang.user_service.utility.validator.AbstractDataValidator;

@Component
public class PaymentRequestValidator extends AbstractDataValidator<PaymentRequest> {
    @Override
    public void validate(PaymentRequest data) {
        checkNumberIsPositive(data.paymentNumber(), "Payment number must be greater than 0");
        checkNotNull(data.price(), "Price cannot be null");
        checkNumberIsPositive(data.price(), "Price must be greater than 0");
        checkEnumValue(data.currency(), Currency.values(), "Invalid Currency type");
    }
}
