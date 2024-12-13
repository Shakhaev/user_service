package school.faang.user_service.controller.project_subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.project_subscription.ProjectFollowerService;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProjectSubscribeController {
    private final UserContext userContext;
    private final ProjectFollowerService projectFollowerService;


    @PutMapping("/followeeId/{followeeId}/projectId/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public void followProject(@PathVariable long projectId, @PathVariable long followeeId) {
        long followerId = userContext.getUserId();
        projectFollowerService.followProject(projectId, followerId, followeeId);
    }
}
