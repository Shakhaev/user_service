package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationRequestDto {
    private Long id;
    @Size(min = 1, max = 4096, message = "Сообщение должно быть от 1 до 4096 символов!")
    @NotBlank(message = "Имя не может быть пустым!")
    private String message;
    private RequestStatus status;
    private String rejectionReason;
    @NotNull(message = "Нужно выбрать хотя бы один навык!")
    private List<Long> skillsIds;
    @Min(value = 1, message = "Нужно выбрать хотя бы одного пользователя кому нужна рекомендация!")
    private Long requesterId;
    @Min(value = 1, message = "Нужно выбрать хотя бы одного пользователя от кого нужна рекомендация!")
    private Long receiverId;
    private String createdAt;
    private String updatedAt;


    @AssertTrue(message = "Запрашивающий и получатель не могут быть одним и тем же пользователем!")
    public boolean isRequesterAndReceiverNotTheSame() {
        return requesterId == null || !requesterId.equals(receiverId);
    }
}
