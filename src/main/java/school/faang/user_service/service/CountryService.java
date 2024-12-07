package school.faang.user_service.service;

import school.faang.user_service.dto.country.CountryDto;

public interface CountryService {

    CountryDto getCountryByName(String countryName);
}
