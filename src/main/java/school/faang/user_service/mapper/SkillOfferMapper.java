package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {
    SkillOffer toEntity(SkillOfferDto skillOfferDto);

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "skill.title", target = "title")
    SkillOfferDto doDto(SkillOffer skillOffer);
}
