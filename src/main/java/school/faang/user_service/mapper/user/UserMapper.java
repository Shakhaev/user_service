package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.model.Student;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "userProfilePic.fileId", target = "userProfilePicFileId")
    @Mapping(source = "premium.id", target = "premiumId")
    UserDto toDto(User user);

    @Mapping(source = "userProfilePicFileId", target = "userProfilePic.fileId")
    @Mapping(source = "premiumId", target = "premium.id")
    User toEntity(UserDto userDto);


    @Mappings({
        @Mapping(target = "username", expression = "java(concatenateName(student.getFirstName(), student.getLastName()))"),
        @Mapping(target = "country", ignore = true)
    })
    User toEntity(Student student);

    default String concatenateName(String firstName, String lastName) {
        return firstName + lastName;
    }

    default String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}