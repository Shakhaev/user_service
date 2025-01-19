package school.faang.user_service.dto.premium.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PremiumBoughtMessage {
    private Long userId;
    private Double cost;
    private Integer days;
    private LocalDateTime purchaseDate;
}
