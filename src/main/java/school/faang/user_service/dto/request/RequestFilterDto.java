package school.faang.user_service.dto.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RequestFilterDto {
    private Long requesterId;
    private Long receiverId;
}
