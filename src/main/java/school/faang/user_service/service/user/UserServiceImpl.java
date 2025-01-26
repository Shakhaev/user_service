package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.controller.user.GetUserRequest;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilter;
import school.faang.user_service.entity.Tariff;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.BusinessException;
import school.faang.user_service.mapper.TariffMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.tariff.TariffService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TariffService tariffService;
    private final UserRepository userRepository;
    private final TariffMapper tariffMapper;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public TariffDto buyUserTariff(TariffDto tariffDto, Long userId) {
        log.info("Start buy user tariff, userId: {}", userId);
        User user = findById(userId);
        if (user.getTariff() != null && user.getTariff().getIsActive()) {
            throw new BusinessException("User already has active tariff");
        }

        tariffDto.setUserId(userId);
        Tariff tariff = tariffService.buyTariff(tariffDto, userId);
        user.setTariff(tariff);
        userRepository.save(user);

        return tariffMapper.toDto(tariff);
    }

    @Override
    @Transactional
    public List<UserDto> findUsersByFilter(GetUserRequest request) {
        List<User> users = userRepository.findAllOrderByTariffAndLimit(request.getLimit(), request.getOffset());

        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(request.getFilter())) {
                users = userFilter.apply(users, request.getFilter());
            }
        }

        return users.stream()
                .peek(user -> tariffService.decrementShows(user.getTariff()))
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

}
