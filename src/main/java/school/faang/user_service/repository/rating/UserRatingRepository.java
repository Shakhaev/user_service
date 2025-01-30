package school.faang.user_service.repository.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.rating.UserRating;

import java.util.List;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {
    List<UserRating> findByUserIdIs(Long userId);

    List<UserRating> findByTypeIdIs(Long typeId);

    List<UserRating> findByUserIdAndTypeIdIs(Long userId, Long typeId);
    UserRating findByUserIdAndTypeNameIs(Long userId, String typeName);

    List<UserRating> findByTypeNameIs(String typeName);
}
