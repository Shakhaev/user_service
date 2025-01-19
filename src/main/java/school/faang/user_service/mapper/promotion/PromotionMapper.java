package school.faang.user_service.mapper.promotion;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.payment.CreateOrderDto;
import school.faang.user_service.dto.promotion.BuyPromotionDto;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.promotion.Promotion;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    PromotionDto toDto(Promotion promotion);

    @Mapping(target = "serviceType", constant = "promotion")
    @Mapping(source = "plan.name", target = "plan")
    CreateOrderDto toCreateOrderDto(BuyPromotionDto buyPromotionDto);
}
