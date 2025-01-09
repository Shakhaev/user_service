package school.faang.user_service.dto;

import lombok.Value;

@Value
public class SubscriptionUserDto {
    Long id;
    String username;
    String email;
}
