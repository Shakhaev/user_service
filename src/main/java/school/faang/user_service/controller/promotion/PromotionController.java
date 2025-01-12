package school.faang.user_service.controller.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.promotion.BuyPromotionDto;
import school.faang.user_service.dto.promotion.EventPromotionResponseDto;
import school.faang.user_service.dto.promotion.PromotedEventResponseDto;
import school.faang.user_service.dto.promotion.UserPromotionResponseDto;
import school.faang.user_service.dto.promotion.UserResponseDto;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.promotion.EventPromotionMapper;
import school.faang.user_service.mapper.promotion.UserPromotionMapper;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.service.promotion.event.EventPromotionService;
import school.faang.user_service.service.promotion.user.UserPromotionService;
import school.faang.user_service.service.user.UserContextService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/promotions")
public class PromotionController {
    private final EventPromotionService eventPromotionService;
    private final UserPromotionService userPromotionService;
    private final EventPromotionMapper eventPromotionMapper;
    private final UserPromotionMapper userPromotionMapper;
    private final UserContextService userContextService;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;

    @PostMapping("/buy")
    public UserPromotionResponseDto buyPromotion(@RequestBody BuyPromotionDto buyPromotionDto) {
        PromotionTariff tariff = PromotionTariff.fromViews(buyPromotionDto.numberOfViews());
        long userId = userContextService.getContextUserId();
        UserPromotion userPromotion = userPromotionService.buyPromotion(userId, tariff);
        return userPromotionMapper.toUserPromotionResponseDto(userPromotion);
    }

    @PostMapping("/events/{id}/buy")
    public EventPromotionResponseDto buyEventPromotion(@PathVariable(name = "id") long eventId,
                                                       @RequestBody BuyPromotionDto buyPromotionDto) {
        PromotionTariff tariff = PromotionTariff.fromViews(buyPromotionDto.numberOfViews());
        long userId = userContextService.getContextUserId();
        EventPromotion eventPromotion = eventPromotionService.buyEventPromotion(userId, eventId, tariff);
        return eventPromotionMapper.toEventPromotionResponseDto(eventPromotion);
    }

    @GetMapping("/per-page")
    public List<UserResponseDto> getPromotedUsersBeforeAllPerPage(@RequestParam(name = "offset") Long offset,
                                                                  @RequestParam(name = "limit") Long limit) {
        return userPromotionService.getPromotedUsersBeforeAllPerPage(offset, limit)
                .stream()
                .map(userMapper::toUserResponseDto)
                .toList();
    }

    @GetMapping("/events/per-page")
    public List<PromotedEventResponseDto> getPromotedEventsBeforeAllPerPage(@RequestParam(name = "offset") Long offset,
                                                                            @RequestParam(name = "limit") Long limit) {
        return eventPromotionService.getPromotedEventsBeforeAllPerPage(offset, limit)
                .stream()
                .map(eventMapper::toPromotedEventResponseDto)
                .toList();
    }
}
