package school.faang.user_service.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import school.faang.user_service.dto.TariffDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ConfigurationProperties(prefix = "user-service")
@Getter
@Setter
public class UserServiceProperties {
    private final static Function<Map.Entry<String, TariffProperties>, TariffDto> TO_TARIFF_DTO_FUNCTION =
            entry -> TariffDto.builder()
                    .shows(entry.getValue().getShows())
                    .priority(entry.getValue().getPriority())
                    .expirePeriod(LocalDateTime.now().plusDays(entry.getValue().days))
                    .plan(entry.getKey())
                    .build();

    private RecommendationRequestProperties recommendationRequest;
    private Map<String, TariffProperties> availableTariffs = new HashMap<>();

    @Getter
    @Setter
    public static class RecommendationRequestProperties {
        private int minMonth;
    }

    @Data
    public static class TariffProperties {
        private int shows;
        private Integer days;
        private Integer priority;
        private BigDecimal price;
        private String currency;
    }

    public List<TariffDto> getListAvailableTariffDtos() {
        return availableTariffs.entrySet().stream()
                .map(TO_TARIFF_DTO_FUNCTION)
                .toList();
    }
}
