package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserSearchResponse;
import school.faang.user_service.model.jpa.Country;
import school.faang.user_service.model.jpa.User;
import school.faang.user_service.model.jpa.event.Rating;
import school.faang.user_service.model.search.user.UserDocument;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GoalMapper.class, EventMapper.class, SkillMapper.class},
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    List<UserDto> toDto(List<User> users);

    UserSearchResponse toSearchResponse(UserDocument userDocument);

    List<UserSearchResponse> toResponseList(List<UserDocument> userDocuments);

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "country", target = "country", qualifiedByName = "toCountryName")
    UserSearchResponse toSearchResponse(User userDocument);

    List<UserSearchResponse> toSearchResponseList(List<User> users);

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "country", target = "country", qualifiedByName = "toCountryName")
    @Mapping(source = "skills", target = "skillNames",
            qualifiedByName = "toSkillNameList")
    @Mapping(source = "ratings", target = "averageRating", qualifiedByName = "toAverageRating")
    UserDocument toUserDocument(User user);

    List<UserDocument> toUserDocumentList(List<User> users);

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
