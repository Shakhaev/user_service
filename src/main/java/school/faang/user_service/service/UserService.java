package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filters.interfaces.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    @Transactional(readOnly = true)
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        Stream<User> users = userRepository.findPremiumUsers();
        var filterUser = filterUser(users, filterDto)
                .map(userMapper::toDto)
                .toList();

        log.info("Found {} premium users", filterUser);

        return filterUser;
    }

    private Stream<User> filterUser(Stream<User> users, UserFilterDto filterDto) {
        for (UserFilter filter : userFilters) {
            if (filter.isAcceptable(filterDto)) {
                users = filter.accept(users, filterDto);
            }
        }
        return users;
    }

}
