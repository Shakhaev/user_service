package school.faang.user_service.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaConstants {
    public final static String PAYMENT_PROMOTION_TOPIC = "payment_promotion";
    public final static String EVENT_KEY = "event";
    public final static String USER_KEY = "user";
}
