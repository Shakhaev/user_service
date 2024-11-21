package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.model.jpa.recommendation.SkillOffer;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {

    @Mapping(target = "skill", ignore = true)
    SkillOffer toEntity(SkillOfferDto skillOfferDto);

    @Mapping(source = "skill.id", target = "skillId")
    SkillOfferDto toDto(SkillOffer skillOffer);
}
