package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.CreateRecommendationResponse;
import school.faang.user_service.dto.recommendation.GetAllGivenRecommendationsResponse;
import school.faang.user_service.dto.recommendation.GetAllUserRecommendationsResponse;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationResponse;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    Recommendation toEntity(RecommendationDto recommendationDto);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOfferIds", qualifiedByName = "mapSkillOffersToIds")
    CreateRecommendationResponse toCreateDto(Recommendation recommendation);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOfferIds", qualifiedByName = "mapSkillOffersToIds")
    UpdateRecommendationResponse toUpdateDto(Recommendation recommendation);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOfferIds", qualifiedByName = "mapSkillOffersToIds")
    GetAllGivenRecommendationsResponse toGetAllGivenDto(Recommendation recommendation);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "skillOffers", target = "skillOfferIds", qualifiedByName = "mapSkillOffersToIds")
    GetAllUserRecommendationsResponse toGetAllUserDto(Recommendation recommendation);

    @Named("mapSkillOffersToIds")
    default List<Long> mapSkillOffersToIds(List<SkillOffer> skillOffers) {
        return skillOffers.stream().map(SkillOffer::getId).toList();
    }
}
