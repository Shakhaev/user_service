package school.faang.user_service.exception.error;

import lombok.Builder;

@Builder
public record ErrorResponse(int code, String message) {
}
