package school.faang.user_service.dto.premium;

import lombok.Builder;

@Builder
public record MessageDto(boolean status,
                         String message,
                         int code) {
}