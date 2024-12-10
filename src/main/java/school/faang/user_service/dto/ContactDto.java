package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import school.faang.user_service.entity.contact.ContactType;

@Builder
public record ContactDto(
        Long id,
        Long userId,
        @NotBlank(message = "Contact cannot be blank")
        String contact,
        //@NotEmpty(message = "Contact cannot be empty")
        ContactType type
) {
}
