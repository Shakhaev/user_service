package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.user.ShortUserWithAvatarDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ShortUserWithAvatarMapper {
    @Mapping(source = "userProfilePic", target = "smallAvatarId", qualifiedByName = "mapToSmallAvatarId")
    ShortUserWithAvatarDto toDto(User user);

    @Named("mapToSmallAvatarId")
    default String mapToSmallAvatarId(UserProfilePic profilePic) {
        return profilePic == null ? null : profilePic.getSmallFileId();
    }
}
