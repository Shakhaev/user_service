package school.faang.user_service.dto.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDto {
    private Long id;

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    @Size(max = 128, message = "Name must not exceed 128 characters")
    private String name;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 4096, message = "Description must not exceed 4096 characters")
    private String description;

    @NotNull(message = "OwnerId must not be null")
    @Min(value = 0, message = "OwnerId must be non-negative")
    private Long ownerId;

    private Long parentProjectId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotNull(message = "Visibility must not be null")
    private ProjectVisibility visibility;

    private String coverImageId;

    private List<Long> teamsIds;

    private List<Long> childrenIds;
}