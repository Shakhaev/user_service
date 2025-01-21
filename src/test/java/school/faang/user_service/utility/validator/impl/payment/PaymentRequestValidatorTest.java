package school.faang.user_service.utility.validator.impl.payment;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.payment_service.Currency;
import school.faang.user_service.dto.payment_service.PaymentRequest;
import school.faang.user_service.exception.DataValidationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentRequestValidatorTest {
    private final PaymentRequestValidator validator = new PaymentRequestValidator();
    String errorMessage = "Data is invalid";

    @Test
    void checkRequestValid() {
        PaymentRequest validRequest = PaymentRequest.builder()
                .paymentNumber(2L)
                .amount(BigDecimal.valueOf(80L))
                .currency(Currency.EUR)
                .build();

        assertDoesNotThrow(() -> validator.validate(validRequest));
    }

    @Test
    void checkZeroPriceThrowsException() {
        BigDecimal price = BigDecimal.ZERO;

        DataValidationException ex = assertThrows(DataValidationException.class, () ->
                validator.checkNumberIsPositive(price, errorMessage));

        assertEquals(errorMessage, ex.getMessage());
    }

    @Test
    void checkNegativePriceThrowsException() {
        BigDecimal price = BigDecimal.valueOf(-1.0);

        DataValidationException ex = assertThrows(DataValidationException.class, () ->
                validator.checkNumberIsPositive(price, errorMessage));

        assertEquals(errorMessage, ex.getMessage());
    }
}