package school.faang.user_service.mapper.promotion;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionRequestDto;
import school.faang.user_service.entity.promotion.Promotion;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    Promotion toEntity(PromotionRequestDto requestDto);

    PromotionDto toDto(Promotion promotion);

    @AfterMapping
    default void setDefaultFields(@MappingTarget Promotion promotion, PromotionRequestDto requestDto) {
        if (promotion.getStartTime() == null) {
            promotion.setStartTime(LocalDateTime.now());
        }
        if (promotion.getCurrentImpressions() == 0) {
            promotion.setCurrentImpressions(0);
        }
        promotion.setActive(true);
    }

    void updatePromotionFromDto(PromotionRequestDto requestDto, @MappingTarget Promotion promotion);
}
