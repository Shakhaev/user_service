package school.faang.user_service.filters.subscription;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.interfaces.UserFilter;

import java.util.stream.Stream;

public class ExperienceFilter implements UserFilter {
    @Override
    public boolean isAcceptable(UserFilterDto userFilterDto) {
        return true;
    }

    @Override
    public Stream<User> accept(Stream<User> users, UserFilterDto userFilterDto) {
        return users.filter(user -> userFilterDto.experienceMin() <= user.getExperience()
                && userFilterDto.experienceMax() >= user.getExperience());
    }
}
