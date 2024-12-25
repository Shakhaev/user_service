package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.jpa.Country;
import school.faang.user_service.repository.jpa.CountryRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepo;

    public Country getCountryById(Long id) {
        return countryRepository.findById(id).orElseThrow(() -> {
                    log.error("Country with id {} not found", id);
                    return new EntityNotFoundException("Country not found");
                }
        );

    public Optional<Country> findCountryById(Long id) {
        return countryRepo.findById(id);
    }
}
