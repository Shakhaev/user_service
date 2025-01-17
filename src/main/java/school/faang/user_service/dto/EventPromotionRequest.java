package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record EventPromotionRequest(@NotNull @Positive
                                    Long userId,
                                    @NotNull @Positive
                                    Long eventId,
                                    @Min(value = 100, message = "Бюджет на день не может быть меньше 100")
                                    Long budgetInDay,
                                    @Positive(message = "Количество дней не может быть меньше 1")
                                    Long countDays) implements Serializable {
}
