package school.faang.user_service.dto;

/**
 * DTO for {@link school.faang.user_service.entity.User}
 */
public record UserRegisterResponse(
        Long id,
        String username,
        String email,
        String phone) {
}