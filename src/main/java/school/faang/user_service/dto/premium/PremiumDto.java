package school.faang.user_service.dto.premium;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PremiumDto {
    private Long id;
    private Long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
