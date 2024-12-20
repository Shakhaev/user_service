package school.faang.user_service.message.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Builder
@Getter
@AllArgsConstructor
public class ProfileViewEvent implements Serializable {
    private long receiverId;
    private long viewerUserId;
}
