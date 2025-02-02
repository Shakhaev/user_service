package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.PromotionPlan;

import java.util.Optional;

@Repository
public interface PromotionPlanRepository extends JpaRepository<PromotionPlan, Long> {

    Optional<PromotionPlan> findPromotionPlanByName(String name);

    Optional<PromotionPlan> findPromotionPlanByPrice(long price);
}
