package school.faang.user_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record FollowerEvent(long followerId, long followeeId, LocalDateTime receivedAt) {
}