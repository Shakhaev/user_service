package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.entity.Tariff;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.BusinessException;
import school.faang.user_service.mapper.TariffMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.tariff.TariffService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final TariffService tariffService;
    private final UserRepository userRepository;
    private final TariffMapper tariffMapper;

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public TariffDto buyUserTariff(TariffDto tariffDto, Long userId) {
        log.info("Start buy user tariff, userId: {}", userId);
        User user = findById(userId);
        if (user.getTariff() != null) {
            throw new BusinessException("User already has tariff");
        }

        tariffDto.setUserId(userId);
        Tariff tariff = tariffService.buyTariff(tariffDto, userId);
        user.setTariff(tariff);
        userRepository.save(user);

        return tariffMapper.toDto(tariff);
    }
}
