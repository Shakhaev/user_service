package school.faang.user_service.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaTopics {
    public final static String PROMOTION_BOUGHT_TOPIC = "promotion_bought";
    public final static String EVENT_KEY = "event";
    public final static String USER_KEY = "user";
}
