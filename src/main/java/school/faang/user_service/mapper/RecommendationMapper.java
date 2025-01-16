package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(target = "author", ignore = true) //, (source = "authorId", target = "author",qualifiedByName = "getAuthor")
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Recommendation toEntity(RecommendationDto recommendationDto);

//    private User getAuthor(User author) {
//        return userRepository.findAll(author.getId());
//    }
}
