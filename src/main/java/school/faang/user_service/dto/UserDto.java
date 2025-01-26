package school.faang.user_service.dto;

public record UserDto(
        String username,
        String aboutMe,
        Integer experience
) {
}
