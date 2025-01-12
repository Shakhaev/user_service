package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionUserMapper {
    User toUserEntity(SubscriptionUserDto subscriptionUserDto);

    SubscriptionUserDto toSubscriptionUserDto(User user);

    List<SubscriptionUserDto> toSubscriptionUserDto(List<User> list);

    List<User> toUserEntity(List<SubscriptionUserDto> list);
}


