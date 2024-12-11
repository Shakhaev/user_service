package school.faang.user_service.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestEvent {
    @JsonProperty("receiverId")
    private long receiverId;

    @JsonProperty("actorId")
    private long actorId;

    private LocalDateTime time;
}
