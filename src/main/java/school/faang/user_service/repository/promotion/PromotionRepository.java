package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.Promotion;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findPromotionByUserId(long userId);

    List<Promotion> findPromotionByEventId(long eventId);

    @Query("SELECT p FROM Promotion p JOIN p.event e WHERE e.title LIKE %:query% AND p.status = 'ACTIVE'")
    List<Promotion> findPromotedEventsByQuery(@Param("query") String query);

    @Query("SELECT p FROM Promotion p JOIN p.user e WHERE e.username LIKE %:query% AND p.status = 'ACTIVE'")
    List<Promotion> findPromotedUsersByQuery(@Param("query") String query);

}
