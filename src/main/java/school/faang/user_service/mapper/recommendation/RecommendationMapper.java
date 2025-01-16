package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.recommendation_dto.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.recommendation_dto.CreateRecommendationResponse;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(target = "skillOffers", ignore = true)
    CreateRecommendationResponse entityToCreateResponse(Recommendation recommendation);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "skillOffers", ignore = true)
    Recommendation createRequestToEntity(CreateRecommendationRequest recommendationDto);
}
