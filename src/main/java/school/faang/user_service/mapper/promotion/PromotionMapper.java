package school.faang.user_service.mapper.promotion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.payment.CreateOrderDto;
import school.faang.user_service.dto.promotion.BuyPromotionDto;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.promotion.Promotion;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    @Mapping(source = "active", target = "isActive")
    PromotionDto toDto(Promotion promotion);

    @Mapping(source = "active", target = "isActive")
    Promotion toEntity(PromotionDto promotionDto);


    @Mapping(target = "serviceType", constant = "promotion")
    @Mapping(source = "plan.name", target = "plan")
    CreateOrderDto toCreateOrderDto(BuyPromotionDto buyPromotionDto);
}
