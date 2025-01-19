package school.faang.user_service.dto.user.message.builder;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.message.ProfileViewEventParticipant;
import school.faang.user_service.dto.user.message.ProfileViewMessage;

import java.util.List;

@Component
public class ProfileViewMessageBuilder {
    public ProfileViewMessage build(Long receiverId, List<ProfileViewEventParticipant> profiles) {
        List<Long> profileIds = profiles.stream()
                .map(ProfileViewEventParticipant::getId)
                .toList();

        return ProfileViewMessage.builder()
                .receiverId(receiverId)
                .profileIds(profileIds)
                .build();
    }
}
