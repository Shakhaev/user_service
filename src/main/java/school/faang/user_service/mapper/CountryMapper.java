package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.country.CountryDto;
import school.faang.user_service.entity.Country;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CountryMapper {

    Country toEntity(CountryDto countryDto);

    CountryDto toDto(Country country);
}
