package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class RecommendationRequestDto {
    private final Long id;
    private final String message;
    private final String status;
    private final List<Long> skills;
    private final Long requesterId;
    private final Long receiverId;
    private final String createdAt;
    private final String updatedAt;
    private final String rejectionReason;

    public Set<Long> getRequesterIdAndReceiverIds() {
        return new HashSet<>(List.of(requesterId, receiverId));
    }
}
