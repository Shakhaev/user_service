package school.faang.user_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.User;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = """
            SELECT COUNT(s.id) FROM users u
            JOIN user_skill us ON us.user_id = u.id
            JOIN skill s ON us.skill_id = s.id
            WHERE u.id = ?1 AND s.id IN (?2)
            """)
    int countOwnedSkills(long userId, List<Long> ids);

    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users u
            JOIN user_premium up ON up.user_id = u.id
            WHERE up.end_date > NOW()
            """)
    Stream<User> findPremiumUsers();

    @Query(nativeQuery = true, value = "SELECT MIN(id) FROM users")
    Long findMinId();

    @Query(nativeQuery = true, value = "SELECT MAX(id) FROM users")
    Long findMaxId();

    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE id BETWEEN ?1 AND ?2")
    Page<User> findAllInRange(long minId, long maxId, Pageable pageable);

    Page<User> findAllByExperienceBetween(Integer experienceAfter, Integer experienceBefore, Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(*) > 0\s
            FROM users\s
            WHERE email = ?1
            """)
    boolean existsByEmail(String email);
}