package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.goal.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.filter.filtersForUserFilterDto.DtoUserFilter;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final List<DtoUserFilter> userFilters;
    private final UserMapper userMapper;


    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto){
        return applyFilter(userRepository.findPremiumUsers(), userFilterDto);
    }

    private List<UserDto> applyFilter(Stream<User> userList, UserFilterDto userFilterDto){
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .flatMap(userFilter -> userFilter.apply(userList, userFilterDto))
                .map(userMapper::userToDto)
                .toList();
    }
}
