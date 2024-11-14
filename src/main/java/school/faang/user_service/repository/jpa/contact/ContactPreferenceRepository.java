package school.faang.user_service.repository.jpa.contact;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.jpa.contact.ContactPreference;

@Repository
public interface ContactPreferenceRepository extends CrudRepository<ContactPreference, Long> {
}
