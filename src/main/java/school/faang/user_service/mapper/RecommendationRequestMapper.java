package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

@Mapper(componentModel = "spring")
public interface RecommendationRequestMapper {

    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "skills", ignore = true)
    RecommendationRequest toRecommendationRequest(RecommendationRequestDto recommendationRequestDto);
}
