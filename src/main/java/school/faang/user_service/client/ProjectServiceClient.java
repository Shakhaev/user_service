package school.faang.user_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.project.ProjectDto;

@FeignClient(name = "project-service", url = "${services.project-service.host}:${services.project-service.port}", configuration = FeignConfig.class)
public interface ProjectServiceClient {
    @GetMapping("/project/{id}")
    ProjectDto getProjectById(@PathVariable long id);
}
