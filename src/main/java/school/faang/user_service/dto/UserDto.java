package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import school.faang.user_service.entity.User;

import java.util.List;

public record UserDto(@Positive @NotNull Long id,
                      @NotNull String username,
                      List<User> mentees,
                      List<User> mentors) {
}