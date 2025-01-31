package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.User;

import java.util.List;

@Repository
public interface MentorshipRepository extends JpaRepository<User, Long> {

    List<User> findMenteesById(Long userId);

    List<User> findMentorsById(Long userId);

}
