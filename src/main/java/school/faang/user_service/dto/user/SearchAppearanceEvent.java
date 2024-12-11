package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class SearchAppearanceEvent {
    private List<Long> userIds;
    private Long searchingUserId;
    private LocalDateTime viewedAt;
}