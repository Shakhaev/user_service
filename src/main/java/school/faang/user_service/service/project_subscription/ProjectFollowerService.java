package school.faang.user_service.service.project_subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.messaging.ProjectFollowerEvent;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.messaging.FollowerEventPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectFollowerService {
    private final ProjectSubscriptionRepository projectSubscriptionRepository;
    private final FollowerEventPublisher publisher;

    public void followProject(long projectId, long followerId, long followeeId) {
        log.info("followProject called, projectId = {}, followerId = {}", projectId, followerId);
        if (Objects.equals(followerId, followeeId)) {
            throw new DataValidationException("You cannot follow yourselves project");
        }
        projectSubscriptionRepository.followProject(followerId, projectId);
        log.info(" projectSubscriptionRepository followProject called without exception");
        publisher.publish(new ProjectFollowerEvent(projectId, followerId, followeeId));
    }
}
