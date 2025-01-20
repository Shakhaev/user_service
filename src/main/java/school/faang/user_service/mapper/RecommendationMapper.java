package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        if (skillOffers == null || skillOffers.isEmpty()) {
            return Collections.emptyList();
        }

        return skillOffers.stream()
                .filter(Objects::nonNull)
                .map(skillOffer -> SkillOfferDto.builder()
                        .id(skillOffer.getId())
                        .skillId(skillOffer.getSkill() != null ? skillOffer.getSkill().getId() : null)
                        .build())
                .collect(Collectors.toList());
    }
}
