package school.faang.user_service.service.promotion;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.enums.promotion.PromotionPlanType;
import school.faang.user_service.enums.promotion.PromotionStatus;
import school.faang.user_service.enums.promotion.PromotionTariff;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.promotion.PromotionPlanRepository;
import school.faang.user_service.repository.promotion.PromotionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromotionSearchServiceImpl implements PromotionSearchService {
    private final PromotionRepository promotionRepository;
    private final PromotionPlanRepository promotionPlanRepository;
    private final PromotionService promotionService;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    @Override
    public List<Object> searchResults(String query, int limit) {
        log.info("Search results by query = {}, limit = {}", query, limit);
        List<Object> result = updateAndGetPromotionViewsFor(query);
        return result.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<Object> updateAndGetPromotionViewsFor(String query) {
        List<Object> result = new ArrayList<>();
        List<Promotion> promotedUsersAndEvents = getPromotedUsersAndEvents(query);

        for (Promotion promotion : promotedUsersAndEvents) {
            if (promotion.getUsedViews() <= getViewsCountByPromotionPlan(promotion)
                    && promotion.getStatus().equals(PromotionStatus.ACTIVE)) {
                if (PromotionPlanType.USER.equals(promotion.getPlanType())) {
                    result.add(userMapper.toDto(promotion.getUser()));
                } else if (PromotionPlanType.EVENT.equals(promotion.getPlanType())) {
                    result.add(eventMapper.toDto(promotion.getEvent()));
                }
                promotionService.updatePromotionViews(promotion.getId());
            }
        }
        return result;
    }

    private List<Promotion> getPromotedUsersAndEvents(String query) {
        List<Promotion> promoted = new ArrayList<>();
        List<Promotion> promotedUsers = getPromotedUsers(query);
        List<Promotion> promotedEvents = getPromotedEvents(query);
        promoted.addAll(promotedUsers);
        promoted.addAll(promotedEvents);
        return promoted;
    }

    private Integer getViewsCountByPromotionPlan(Promotion promotion) {
        return getPromotionPlanByTariff(promotion.getTariff()).getViewsCount();
    }

    private List<Promotion> getPromotedEvents(String query) {
        return promotionRepository.findPromotedEventsByQuery(query);
    }

    private List<Promotion> getPromotedUsers(String query) {
        return promotionRepository.findPromotedUsersByQuery(query);
    }

    private PromotionPlan getPromotionPlanByTariff(PromotionTariff tariff) {
        return promotionPlanRepository.findPromotionPlanByName(tariff.getValue()).orElseThrow(() ->
                new EntityNotFoundException(
                        String.format("Promotion plan with tariff = %s not found", tariff.getValue())));
    }
}