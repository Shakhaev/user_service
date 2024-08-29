package school.faang.user_service.mapper.premium;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PremiumMapper {

    @Mapping(source = "user.id", target = "userId")
    PremiumDto toDto(Premium premium);

    List<PremiumDto> toDto(List<Premium> premiums);

    @Mapping(source = "userId", target = "user.id")
    Premium toEntity(PremiumDto premiumDto);
}
