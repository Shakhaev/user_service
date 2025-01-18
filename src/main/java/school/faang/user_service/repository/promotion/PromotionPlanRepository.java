package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.PromotionPlan;

@Repository
public interface PromotionPlanRepository extends JpaRepository<PromotionPlan, Long> {

    PromotionPlan findPromotionPlanByName(String name);

    PromotionPlan findPromotionPlanByPrice(long price);
}
