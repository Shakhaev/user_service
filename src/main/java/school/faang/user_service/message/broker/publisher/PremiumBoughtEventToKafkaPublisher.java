package school.faang.user_service.message.broker.publisher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.premium.message.PremiumBoughtMessage;
import school.faang.user_service.dto.premium.message.builder.PremiumBoughtMessageBuilder;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.enums.publisher.PublisherType;

import java.time.temporal.ChronoUnit;

import static school.faang.user_service.enums.publisher.PublisherType.PREMIUM_BOUGHT;

@Getter
@RequiredArgsConstructor
@Component
public class PremiumBoughtEventToKafkaPublisher implements EventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PremiumBoughtMessageBuilder builder;
    private final PublisherType type = PREMIUM_BOUGHT;

    @Value("${spring.kafka.topic.user.premium_bought}")
    private String topicName;

    @Override
    public void publish(JoinPoint joinPoint, Object returnedValue) {
        Premium premium = (Premium) returnedValue;

        long userId = premium.getUser().getId();
        int days = (int) ChronoUnit.DAYS.between(premium.getStartDate(), premium.getEndDate());
        double cost = PremiumPeriod.fromDays(days).getCost();

        PremiumBoughtMessage message = builder.build(userId, cost, days);
        kafkaTemplate.send(topicName, message);
    }
}
