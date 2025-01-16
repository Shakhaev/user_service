package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.Assert.assertThrows;

public class SubscriptionValidatorTest {

    private final SubscriptionValidator subscriptionValidator = new SubscriptionValidator();

    @Test
    public void testValidateNotSelfSubscription() {
        String exceptionMessage = "Some exception message";
        long followerId = 1L;
        long followeeId = 1L;

        assertThrows(DataValidationException.class, () -> {
            subscriptionValidator.validateNotSelfSubscription(exceptionMessage, followerId, followeeId);
        });
    }
}
