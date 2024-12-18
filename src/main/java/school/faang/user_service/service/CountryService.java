package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.jpa.Country;
import school.faang.user_service.repository.jpa.CountryRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepo;

    public Optional<Country> findCountryById(Long id) {
        return countryRepo.findById(id);
    }
}
