package school.faang.user_service.dto.user;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SearchAppearanceEvent(List<Long> userIds, long authorId, LocalDateTime viewTime) {
}
