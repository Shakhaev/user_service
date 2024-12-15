package school.faang.user_service.dto;

import lombok.Builder;

@Builder
public record FollowerProjectEvent(
    long followerId,
    long projectId,
    long ownerId
) {
}
