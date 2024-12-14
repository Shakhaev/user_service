package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PremiumBoughtEvent {

    private Long userId;
    private double amount;
    private int duration;
    private LocalDateTime endDate;
}