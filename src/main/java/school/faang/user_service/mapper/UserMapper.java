package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserProfileCreateDto;
import school.faang.user_service.dto.user.UserProfileResponseDto;
import school.faang.user_service.dto.user.UserSearchResponse;
import school.faang.user_service.message.event.reindex.user.UserDocument;
import school.faang.user_service.model.jpa.Country;
import school.faang.user_service.model.jpa.User;
import school.faang.user_service.model.jpa.event.Rating;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GoalMapper.class, EventMapper.class, SkillMapper.class, CountryMapper.class},
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    List<UserDto> toDto(List<User> users);

    UserSearchResponse toSearchResponse(UserDocument userDocument);

    List<UserSearchResponse> toResponseList(List<UserDocument> userDocuments);

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "country", target = "country", qualifiedByName = "toCountryName")
    UserSearchResponse toSearchResponse(User user);

    List<UserSearchResponse> toSearchResponseList(List<User> users);

    @Mapping(source = "id", target = "resourceId")
    @Mapping(source = "country", target = "country", qualifiedByName = "toCountryName")
    @Mapping(source = "skills", target = "skillNames",
            qualifiedByName = "toSkillNameList")
    @Mapping(source = "ratings", target = "averageRating", qualifiedByName = "toAverageRating")
    UserDocument toUserDocument(User user);

    List<UserDocument> toUserDocumentList(List<User> users);

    UserProfileResponseDto toUserProfileResponseDto(User user);

    @Mapping(target = "country", ignore = true)
    User toEntity(UserProfileCreateDto dto);

    @Named("toAverageRating")
    default Double toAverageRating(List<Rating> ratings) {
        return ratings.stream()
                .map(Rating::getRate)
                .mapToDouble(Double::valueOf)
                .average()
                .orElse(0);
    }

    @Named("toCountryName")
    default String toCountryName(Country country) {
        return country.getTitle();
    }
}
