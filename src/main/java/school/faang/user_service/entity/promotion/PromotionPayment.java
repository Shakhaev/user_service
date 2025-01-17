package school.faang.user_service.entity.promotion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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
    private String id;

    @Column(name = "user_id")
    private Long userId;

    @Column
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PromotionPaymentStatus status;

    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    private PromotionPaymentType paymentType;

    @OneToOne(mappedBy = "promotionPayment")
    private Promotion promotion;
}