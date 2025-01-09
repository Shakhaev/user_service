package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.User;

import java.util.List;

public interface MentorshipRepository extends CrudRepository<User, Long> {
}
