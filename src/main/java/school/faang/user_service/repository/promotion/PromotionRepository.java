package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.Promotion;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> getPromotionByUserId(long userId);

    List<Promotion> getPromotionByEventId(long eventId);

    List<Promotion> getPromotionByStatus(String status);
}
