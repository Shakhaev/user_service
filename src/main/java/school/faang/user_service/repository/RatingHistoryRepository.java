package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.rating.RatingHistory;

@Repository
public interface RatingHistoryRepository extends JpaRepository<RatingHistory, Long> { }
