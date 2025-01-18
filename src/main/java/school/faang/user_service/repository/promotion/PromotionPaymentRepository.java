package school.faang.user_service.repository.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.promotion.PromotionPayment;

import java.util.UUID;

@Repository
public interface PromotionPaymentRepository extends JpaRepository<PromotionPayment, Long> {

    PromotionPayment findPromotionPaymentById(UUID id);
}
