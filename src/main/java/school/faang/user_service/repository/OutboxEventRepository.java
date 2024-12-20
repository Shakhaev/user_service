package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.OutboxEvent;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findAllByProcessedFalse();
}
