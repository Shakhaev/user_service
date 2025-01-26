package school.faang.user_service.service.goal.filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.goal.filter.fiilterabs.UserFilter;

import java.util.stream.Stream;

public class SkillFilter implements UserFilter {
    @Override
    public boolean isAcceptable(UserFilterDto userFilterDto) {
        return userFilterDto.skillPattern() != null;
    }

    @Override
    public Stream<User> accept(Stream<User> users, UserFilterDto userFilterDto) {
        return users.filter(user -> userFilterDto.skillPattern() == null
                || user.getSkills().stream()
                .allMatch(skill -> matchesPattern(userFilterDto.skillPattern(), skill.getTitle())));
    }

    private boolean matchesPattern(String pattern, String value) {
        return pattern == null || value.matches(pattern);
    }
}
