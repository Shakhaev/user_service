package school.faang.user_service.model.search.user;

import lombok.Builder;

import java.util.List;

@Builder
public record UserFilter(
        String query,
        List<String> skillNames,
        Integer experienceFrom,
        Integer experienceTo
) {
}
