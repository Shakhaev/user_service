package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationMapper {

    @Mapping(source = "skillOffers", target = "skillOffers", qualifiedByName = "mapSkillOffers")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "author.id", target = "authorId")
    RecommendationDto toDto(Recommendation recommendation);

    @Mapping(target = "skillOffers", ignore = true)
    @Mapping(source = "receiverId", target = "receiver.id")
    @Mapping(source = "authorId", target = "author.id")
    Recommendation toEntity(RecommendationDto recommendationDto);

    @Named("mapSkillOffers")
    default List<SkillOfferDto> mapSkillOffers(List<SkillOffer> skillOffers) {
        return skillOffers
                .stream()
                .map(skillOffer -> SkillOfferDto
                        .builder()
                        .id(skillOffer.getId())
                        .skillId(skillOffer.getSkill().getId())
                        .build())
                .toList();
    }
}