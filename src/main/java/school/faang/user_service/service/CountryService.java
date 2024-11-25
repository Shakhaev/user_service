package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Country;
import school.faang.user_service.repository.CountryRepository;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    public Country getOrCreateCountry(String title) {
        if (!countryRepository.existsByTitle(title)) {
            return countryRepository.save(Country.builder().title(title).build());
        }
        return countryRepository.getByTitle(title);
    }
}
