package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.skill_offer_dto.CreateSkillOfferRequest;
import school.faang.user_service.dto.recommendation.skill_offer_dto.CreateSkillOfferResponse;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {
    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "recommendation.id", target = "recommendationId")
    CreateSkillOfferResponse entityToCreateResponse(SkillOffer skillOffer);

    @Mapping(target = "skill", ignore = true)
    @Mapping(target = "recommendation", ignore = true)
    SkillOffer createRequestToEntity(CreateSkillOfferRequest skillOfferDto);
}
