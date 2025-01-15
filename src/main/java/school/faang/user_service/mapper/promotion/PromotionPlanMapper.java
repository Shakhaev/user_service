package school.faang.user_service.mapper.promotion;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.promotion.PromotionPlanDto;
import school.faang.user_service.entity.promotion.PromotionPlan;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PromotionPlanMapper {

    PromotionPlanDto toDto(PromotionPlan promotionPlan);

    PromotionPlan toEntity(PromotionPlanDto promotionPlanDto);
}
