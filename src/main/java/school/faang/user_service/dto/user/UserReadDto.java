package school.faang.user_service.dto.user;

import lombok.Builder;

@Builder
public record UserReadDto(
        Long id,
        String username,
        String email
) {
}
