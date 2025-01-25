package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.BuyTariffRequest;
import school.faang.user_service.dto.TariffDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.properties.UserServiceProperties;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${user-service.api-version}/user")
public class UserController {
    private final UserService userService;
    private final UserServiceProperties userServiceProperties;

    @PostMapping("/buy-tariff")
    public ResponseEntity<TariffDto> buyTariff(@RequestBody BuyTariffRequest request) {
        return ResponseEntity.ok(userService.buyUserTariff(request.tariffDto(), request.id()));
    }

    @GetMapping("/tariffs")
    public List<TariffDto> getAvailableTariffs() {
        return userServiceProperties.getListAvailableTariffDtos();
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestBody GetUserRequest request) {
        return userService.findUsersByFilter(request.getFilter(), request.getLimit(), request.getOffset());
    }
}
