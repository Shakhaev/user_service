package school.faang.user_service.entity.promotion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.promotion.PromotionPaymentStatus;
import school.faang.user_service.enums.promotion.PromotionPaymentType;

import java.math.BigDecimal;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotion_payment")
public class PromotionPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "promotion_id")
    private long promotionId;

    @Column
    private BigDecimal amount;

    private PromotionPaymentStatus status;

    @Column(name = "payment_type")
    private PromotionPaymentType paymentType;
}