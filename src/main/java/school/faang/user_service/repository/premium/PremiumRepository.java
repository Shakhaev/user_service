package school.faang.user_service.repository.premium;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.premium.Premium;

public interface PremiumRepository extends CrudRepository<Premium, Long> {

  boolean existsByUserId(long userId);

  List<Premium> findAllByEndDateBefore(LocalDateTime endDate);
}
