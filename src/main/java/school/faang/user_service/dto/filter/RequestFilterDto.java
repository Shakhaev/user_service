package school.faang.user_service.dto.filter;

import lombok.Data;

@Data
public class RequestFilterDto {
    private Long id;
    private Long requesterId;
    private Long receiverId;
}
