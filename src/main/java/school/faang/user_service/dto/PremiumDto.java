package school.faang.user_service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class PremiumDto {
    private Long id;
    private String userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}

