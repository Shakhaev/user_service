package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.TariffDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("${user-service.api-version}/promo")
public class TariffController {

//    @PostMapping("/buy")
//    public ResponseEntity<Void> buyTariff(@RequestBody TariffDto tariffDto) {
//
//    }
}
