package school.faang.user_service.mapper.promotion;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.promotion.Promotion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PromotionMapper {

    PromotionDto toDto(Promotion promotion);

    Promotion toEntity(PromotionDto promotionDto);
}
