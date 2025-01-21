package school.faang.user_service.dto.user;

import lombok.Builder;

import java.util.List;

@Builder
public record BanUsersDto(List<Long> usersIds) {
}