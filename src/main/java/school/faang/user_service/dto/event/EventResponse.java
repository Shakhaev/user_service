package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EventResponse(@NotNull Long id,
                            @NotNull String title,
                            String description) {
}
