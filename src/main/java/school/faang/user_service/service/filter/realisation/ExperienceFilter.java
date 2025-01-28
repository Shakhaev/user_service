package school.faang.user_service.service.filter.realisation;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.filter.UserFilter;

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
