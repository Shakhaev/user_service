package school.faang.user_service.dto.subscription;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubscriptionUserDto {
    private Long id;
    private String username;
    private String email;
}
