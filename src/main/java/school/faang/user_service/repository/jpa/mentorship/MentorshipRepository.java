package school.faang.user_service.repository.jpa.mentorship;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.jpa.User;

@Repository
public interface MentorshipRepository extends CrudRepository<User, Long> {
}
