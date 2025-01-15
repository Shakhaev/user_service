package school.faang.user_service.entity.promotion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.enums.promotion.PromotionStatus;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotion")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "event_id")
    private long event_id;

    @Column(name = "promotion_plan")
    @OneToMany
    private PromotionPlan promotionPlan;

    @Column(name = "remaining_views")
    private int remainingViews;

    private PromotionStatus status;

    @Column(name = "payment_id")
    @OneToOne(mappedBy = "promotion")
    private PromotionPayment promotionPayment;
}
