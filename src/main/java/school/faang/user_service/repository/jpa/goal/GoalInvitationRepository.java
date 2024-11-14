package school.faang.user_service.repository.jpa.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.model.jpa.goal.GoalInvitation;

@Repository
public interface GoalInvitationRepository extends JpaRepository<GoalInvitation, Long> {
}