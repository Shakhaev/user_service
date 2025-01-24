package school.faang.user_service.service.user;

import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.entity.User;

public interface UserService {
    User findById(Long userId);

    TariffDto buyUserTariff(TariffDto tariffDto, Long userId);
}
