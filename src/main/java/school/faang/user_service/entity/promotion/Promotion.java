package school.faang.user_service.entity.promotion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.enums.promotion.PromotionPlanType;
import school.faang.user_service.enums.promotion.PromotionStatus;
import school.faang.user_service.enums.promotion.PromotionTariff;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotion")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Enumerated(EnumType.STRING)
    private PromotionTariff tariff;

    @Column(name = "plan_type")
    @Enumerated(EnumType.STRING)
    private PromotionPlanType planType;

    @Enumerated(EnumType.STRING)
    private PromotionStatus status;

    @Column(name = "used_views")
    private Integer usedViews;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "payment_id")
    private PromotionPayment promotionPayment;
}
