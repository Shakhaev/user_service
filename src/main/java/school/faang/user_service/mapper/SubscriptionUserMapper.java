package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionUserMapper {
    User toEntity(SubscriptionUserDto subscriptionUserDto);

    SubscriptionUserDto toDto(User user);

    List<SubscriptionUserDto> toDto(List<User> list);

    List<User> toEntity(List<SubscriptionUserDto> list);
}


