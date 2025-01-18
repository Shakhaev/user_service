package school.faang.user_service.dto;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record UserFilterDto(String namePattern, String aboutPattern, String emailPattern, String contactPattern,
                            String countryPattern, String cityPattern, String phonePattern, String skillPattern,
                            @Positive int experienceMin, @Positive int experienceMax, @Positive int page,
                            @Positive int pageSize) {
}
