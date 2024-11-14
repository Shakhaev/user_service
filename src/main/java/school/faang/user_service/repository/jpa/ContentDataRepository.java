package school.faang.user_service.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.jpa.ContentData;

@Repository
public interface ContentDataRepository extends JpaRepository<ContentData, Long> {
}
