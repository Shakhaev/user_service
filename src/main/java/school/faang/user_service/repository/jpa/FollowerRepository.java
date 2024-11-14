package school.faang.user_service.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.jpa.User;

@Repository
public interface FollowerRepository extends JpaRepository<User, Long> {
}
