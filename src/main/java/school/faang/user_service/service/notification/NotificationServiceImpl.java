package school.faang.user_service.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.SearchAppearanceEvent;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.user.SearchAppearanceEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SearchAppearanceEventPublisher searchAppearanceEventPublisher;
    private final UserContext userContext;
    private final UserMapper userMapper;

    @Override
    @Async("notificationPublisherExecutor")
    public void publish(List<User> users) {
        SearchAppearanceEvent event = SearchAppearanceEvent.builder()
                .authorId(userContext.getUserId())
                .userIds(userMapper.map(users))
                .viewTime(LocalDateTime.now())
                .build();
        searchAppearanceEventPublisher.publish(event);
        log.info("Published event to notification service, data: {}", event.toString());
    }
}
