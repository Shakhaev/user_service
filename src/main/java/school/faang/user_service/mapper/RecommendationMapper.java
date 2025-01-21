package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    Recommendation toEntity(RecommendationDto recommendationDto);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    RecommendationDto toDto(Recommendation recommendation);
}
