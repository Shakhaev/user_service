package school.faang.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.dto.entity.Country;

public interface CountryRepository extends CrudRepository<Country, Long> {
}