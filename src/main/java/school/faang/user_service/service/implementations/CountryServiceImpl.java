package school.faang.user_service.service.implementations;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.country.CountryDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.mapper.CountryMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.service.CountryService;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    @Override
    public CountryDto getCountryByName(String countryName) {
        Country country = countryRepository.findAll().stream()
                .filter(countryItem -> countryItem.getTitle().equals(countryName))
                .findFirst().orElseThrow(() -> new NoSuchElementException(
                        "Страна с названием " + countryName + " не найдена в базе данных"
                ));
        return countryMapper.toDto(country);
    }
}
