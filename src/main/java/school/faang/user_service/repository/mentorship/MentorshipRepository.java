package school.faang.user_service.repository.mentorship;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.Mentorship;

public interface MentorshipRepository extends CrudRepository<Mentorship, Long> {

    @Query(nativeQuery = true, value = """
            UPDATE mentorship SET mentor_id = mentee_id
            WHERE mentor_id = :userId
            """)
    @Modifying
    void deactivateMentorship(long userId);
}
