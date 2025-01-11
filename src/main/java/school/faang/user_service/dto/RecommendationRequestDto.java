package school.faang.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestDto {
    @Min(value = 1, message = "ID должен быть положительным числом.")
    private Long id;

    @NotBlank(message = "Сообщение не может быть пустым.")
    @Size(max = 4096, message = "Сообщение не может превышать 4096 символов.")
    private String message;

    @NotNull(message = "Статус запроса обязателен.")
    private RequestStatus status;

    @NotEmpty(message = "Список навыков не может быть пустым.")
    private List<SkillRequest> skills;

    @Min(value = 1, message = "ID отправителя должен быть положительным числом.")
    private long requesterId;

    @Min(value = 1, message = "ID получателя должен быть положительным числом.")
    private long receiverId;

    @Size(max = 4096, message = "Причина отказа не может превышать 4096 символов.")
    private String rejectionReason;

    @PastOrPresent(message = "Дата создания не может быть из будущего.")
    private LocalDateTime createdAt;

    @PastOrPresent(message = "Дата обновления не может быть из будущего.")
    private LocalDateTime updatedAt;

    private Recommendation recommendation;
}
