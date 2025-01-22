package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.CreateRecommendationResponse;
import school.faang.user_service.dto.recommendation.GetAllRecommendationsResponse;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequest;
import school.faang.user_service.dto.recommendation.UpdateRecommendationResponse;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {
    @Mapping(target="id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    Recommendation fromCreateRequest(CreateRecommendationRequest createRequest);

    @Mapping(target="id", source="id")
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    Recommendation fromUpdateRequest(UpdateRecommendationRequest updateRequest);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOfferIds", qualifiedByName = "mapSkillOffersToIds")
    CreateRecommendationResponse toCreateResponse(Recommendation recommendation);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOfferIds", qualifiedByName = "mapSkillOffersToIds")
    UpdateRecommendationResponse toUpdateResponse(Recommendation recommendation);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOfferIds", qualifiedByName = "mapSkillOffersToIds")
    GetAllRecommendationsResponse toGetAllResponse(Recommendation recommendation);

    @Named("mapSkillOffersToIds")
    default List<Long> mapSkillOffersToIds(List<SkillOffer> skillOffers) {
        return skillOffers.stream().map(SkillOffer::getId).toList();
    }
}
