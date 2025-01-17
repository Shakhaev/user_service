package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.PromotionPayment;

@Repository
public interface PromotionPaymentRepository extends JpaRepository<PromotionPayment, Long> {
    PromotionPayment findPromotionPaymentById(String id);
}
