package school.faang.user_service.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class PremiumDto {
    private Long id;

    private String userId;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime endDate;
}

