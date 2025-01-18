package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.enums.promotion.PromotionStatus;
import school.faang.user_service.enums.promotion.PromotionTariff;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.promotion.PromotionPlanRepository;
import school.faang.user_service.repository.promotion.PromotionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Object> result = new ArrayList<>();
        List<Promotion> promotedUsers = getPromotedUsers(query);
        List<Promotion> promotedEvents = getPromotedEvents(query);

        for (Promotion promotion : promotedUsers) {
            if (promotion.getUsedViews() <= getPromotionPlanByTariff(promotion.getTariff()).getViewsCount()) {
                result.add(userMapper.toDto(promotion.getUser()));
                promotionService.updatePromotionViews(promotion.getId());
            }
        }
        for (Promotion promotion : promotedEvents) {
            if (promotion.getUsedViews() <= getPromotionPlanByTariff(promotion.getTariff()).getViewsCount()
                    && promotion.getStatus().equals(PromotionStatus.ACTIVE)) {
                result.add(eventMapper.toDto(promotion.getEvent()));
                promotionService.updatePromotionViews(promotion.getId());
            }
        }
        return result.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<Promotion> getPromotedEvents(String query) {
        return promotionRepository.findAll().stream()
                .filter(promotion -> promotion.getEvent() != null)
                .filter(promotion -> query.contains(promotion.getEvent().getTitle()))
                .filter(promotion -> promotion.getStatus().equals(PromotionStatus.ACTIVE))
                .toList();
    }

    private List<Promotion> getPromotedUsers(String query) {
        return promotionRepository.findAll().stream()
                .filter(promotion -> promotion.getUser() != null)
                .filter(promotion -> query.contains(promotion.getUser().getUsername()))
                .filter(promotion -> promotion.getStatus().equals(PromotionStatus.ACTIVE))
                .toList();
    }

    private PromotionPlan getPromotionPlanByTariff(PromotionTariff tariff) {
        return promotionPlanRepository.findPromotionPlanByName(tariff.getValue());
    }
}