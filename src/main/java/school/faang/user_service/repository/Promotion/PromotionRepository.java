package school.faang.user_service.repository.Promotion;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.promotion.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.entity.promotion.TargetType;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Query("SELECT p FROM Promotion p WHERE p.userId = :userId AND p.currentImpressions < p.impressionsLimit")
    List<Promotion> findActivePromotionsByUserId(Long userId);

    List<Promotion> findByTargetAndTargetId(TargetType target, Long targetId);

    @Query("SELECT p FROM Promotion p WHERE p.userId = :userId " +
            "AND p.target = :target AND p.targetId = :targetId " +
            "AND p.currentImpressions < p.impressionsLimit")
    List<Promotion> findActivePromotionByUserAndTarget(Long userId, TargetType targetType);

    @Query("SELECT p FROM Promotion p WHERE p.currentImpressions >= p.impressionsLimit")
    List<Promotion> findExpiredPromotions();

    @Modifying
    @Query("DELETE FROM Promotion p WHERE p.currentImpressions >= p.impressionsLimit")
    void deleteExpiredPromotions();

    List<PromotionPlan> findByImpressionsGreaterThanEqual(int impressions);


}


