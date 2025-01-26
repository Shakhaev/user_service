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
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${user-service.api-version}/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/buy-tariff")
    public ResponseEntity<TariffDto> buyTariff(@RequestBody BuyTariffRequest request) {
        return ResponseEntity.ok(userService.buyUserTariff(request.tariffDto(), request.id()));
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestBody GetUserRequest request) {
        return userService.findUsersByFilter(request);
    }
}
