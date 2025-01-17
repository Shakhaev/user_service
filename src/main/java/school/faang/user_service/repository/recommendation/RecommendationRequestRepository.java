package school.faang.user_service.repository.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RecommendationRequestRepository extends JpaRepository<RecommendationRequest, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM recommendation_request
            WHERE requester_id = ?1 AND receiver_id = ?2 AND status = 1
            ORDER BY created_at DESC
            LIMIT 1
            """)
    Optional<RecommendationRequest> findLatestPendingRequest(long requesterId, long receiverId);

    @Query(nativeQuery = true, value = """
            SELECT CASE WHEN EXISTS(
                SELECT * FROM recommendation_request
                         WHERE requester_id = ?1 AND receiver_id = ?1 AND created_at > ?2)
                THEN CAST(1 AS BIT)
                ELSE CAST(0 AS BIT) END
            """)
    Boolean isLatestPendingRequestCreatedAfterThenExists(long userId, LocalDateTime dateTime);
}