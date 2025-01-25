package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {
    SkillOfferDto toDto(SkillOffer skillOffer);

    SkillOffer toEntity(SkillOfferDto skillOfferDto);
}
