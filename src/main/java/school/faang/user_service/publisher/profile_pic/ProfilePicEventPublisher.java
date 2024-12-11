package school.faang.user_service.publisher.profile_pic;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.ProfilePicEvent;
import school.faang.user_service.publisher.EventPublisherAbstract;

@Component
@Slf4j
public class ProfilePicEventPublisher extends EventPublisherAbstract<ProfilePicEvent> {

    @Value("${spring.data.redis.channels.profile-pic-channel}")
    private String profilePic;


    public ProfilePicEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(ProfilePicEvent event) {
        handleEvent(event, profilePic);
    }

}
