package school.faang.user_service.repository.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

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
            INSERT INTO recommendation_request (requester_id, receiver_id, message, status, created_at, updated_at)
            VALUES (?1, ?2, ?3, 0, NOW(), NOW())
            """)
    @Modifying
    RecommendationRequest create(long requesterId, long receiverId, String message);

    @Query(nativeQuery = true, value = """
            SELECT EXISTS(
                SELECT 1
                FROM recommendation_request
                WHERE requester_id = ?1 AND receiver_id = ?2
                AND created_at > DATE_SUB(CURRENT_DATE, INTERVAL 6 MONTH)
            )
            """)
    boolean existsRequestWithinSixMonths(long requesterId, long receiverId);

}