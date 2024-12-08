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

    public void followProject(ProjectFollowerEvent event) {
        log.info("followProject called, event = {}", event);
        if (Objects.equals(event.getFollowerId(), event.getFolloweeId())) {
            throw new DataValidationException("You cannot follow yourselves project");
        }
        projectSubscriptionRepository.followProject(event.getFollowerId(), event.getProjectId());
        log.info(" projectSubscriptionRepository followProject called without exception");
        publisher.publish(event);
    }
}
