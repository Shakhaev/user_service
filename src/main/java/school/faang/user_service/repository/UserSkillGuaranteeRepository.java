package school.faang.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.UserSkillGuarantee;

import java.util.List;

@Repository
public interface UserSkillGuaranteeRepository extends CrudRepository<UserSkillGuarantee, Long> {
    List<UserSkillGuarantee> findByUserIdAndSkillId(Long userId, Long skillId);
}