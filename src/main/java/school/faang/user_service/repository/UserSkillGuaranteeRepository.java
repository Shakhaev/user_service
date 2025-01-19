package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.UserSkillGuarantee;

public interface UserSkillGuaranteeRepository extends JpaRepository<UserSkillGuarantee, Long> {
    UserSkillGuarantee findByUserIdAndSkillId(long userId, long skillId);

    @Query(nativeQuery = true, value = """
            UPDATE user_skill_guarantee SET guarantor_id = :guarantorId
            WHERE user_id = :userId AND skill_id = :skillId
            """)
    @Modifying
    void updateGuarantor(long userId, long skillId, long guarantorId);
}