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
import school.faang.user_service.enums.promotion.Currency;
import school.faang.user_service.enums.promotion.PromotionPaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotion_payment")
public class PromotionPayment {

    @Id
    private UUID id;

    @Column(name = "user_id")
    private Long userId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private PromotionPaymentStatus status;

    @OneToOne(mappedBy = "promotionPayment")
    private Promotion promotion;
}