package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface SubscriptionUserMapper {
    User toEntity(SubscriptionUserDto subscriptionUserDto);

    SubscriptionUserDto toDto(User user);
}


