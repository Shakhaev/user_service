package school.faang.user_service.service.project_subscription;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.ProjectServiceClient;
import school.faang.user_service.dto.messaging.ProjectFollowerEvent;
import school.faang.user_service.dto.project.ProjectDto;
import school.faang.user_service.messaging.FollowerEventPublisher;
import school.faang.user_service.repository.ProjectSubscriptionRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectFollowerService {
    private final ProjectSubscriptionRepository projectSubscriptionRepository;
    private final FollowerEventPublisher publisher;
    private final ProjectServiceClient projectServiceClient;

    public void followProject(long projectId, long followerId) {
        log.info("followProject called, projectId = {}, followerId = {}", projectId, followerId);
        ProjectDto followee;
        try {
            followee = projectServiceClient.getProjectById(projectId);
        } catch (Exception e) {
            log.error("Cannot extract project with id = {}, error. = {}", projectId, e.getMessage());
            throw new EntityNotFoundException(e);
        }
        projectSubscriptionRepository.followProject(followerId, projectId);
        log.info(" projectSubscriptionRepository followProject called without exception");
        publisher.publish(new ProjectFollowerEvent(projectId, followerId, followee.getId()));
    }
}
