package school.faang.user_service.repository.jpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.jpa.Country;

@Repository
public interface CountryRepository extends CrudRepository<Country, Long> {
}