package school.faang.user_service.mapper.promotion;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.promotion.PromotionPaymentDto;
import school.faang.user_service.entity.promotion.PromotionPayment;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PromotionPaymentMapper {

    PromotionPaymentDto toDto(PromotionPayment promotionPayment);

    PromotionPayment toEntity(PromotionPaymentDto promotionPaymentDto);
}