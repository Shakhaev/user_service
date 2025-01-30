package school.faang.user_service.filters.subscription;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.interfaces.UserFilter;

import java.util.stream.Stream;

@Component
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