package school.faang.user_service.service.promotion;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.PromotionPaymentDto;
import school.faang.user_service.dto.promotion.PromotionPlanDto;
import school.faang.user_service.dto.promotion.PromotionRequestDto;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.enums.promotion.PromotionPaymentStatus;
import school.faang.user_service.enums.promotion.PromotionPlanType;
import school.faang.user_service.enums.promotion.PromotionStatus;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.mapper.promotion.PromotionPaymentMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotion.PromotionRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionPlanService promotionPlanService;
    private final PromotionPaymentService promotionPaymentService;
    private final PromotionMapper promotionMapper;
    private final PromotionPaymentMapper promotionPaymentMapper;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<PromotionResponseDto> getPromotionsByUser(long userId) {
        return promotionRepository.getPromotionByUserId(userId).stream()
                .map(promotionMapper::toDto)
                .toList();
    }

    @Override
//    @Transactional
    public PromotionResponseDto createPromotion(PromotionRequestDto dto) {
        checkMatchesPlanWithUserOrEvent(dto);

        PromotionPaymentDto newPayment = promotionPaymentService.sendAndCreate(dto);

        var promotion = createPromotion(dto, newPayment, getPromotionStatus(newPayment.getStatus()));
        return promotionMapper.toDto(promotionRepository.save(promotion));
    }

    private void checkMatchesPlanWithUserOrEvent(PromotionRequestDto dto) {
        Long eventId = dto.getEventId();
        Long userId = dto.getUserId();
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new EntityNotFoundException(String.format("User with id = %d doesn't exists", userId));
        }
        if (eventId != null) {
            Optional<Event> event = eventRepository.findById(eventId);
            if (PromotionPlanType.EVENT.equals(dto.getPlanType()) && event.isPresent()) {
                if (!user.get().getOwnedEvents().contains(event.get())) {
                    throw new EntityNotFoundException(String.format("User haven't event with id = %d", eventId));
                }
            }
        } else {
            throw new EntityNotFoundException(String.format("Event haven't event with id = %d", eventId));
        }
    }

    private Promotion createPromotion(PromotionRequestDto dto, PromotionPaymentDto payment, PromotionStatus status) {
        PromotionPlanDto promotionPlan = promotionPlanService.getPromotionPlanByName(dto.getTariff().getValue());
        return Promotion.builder()
                .userId(dto.getUserId())
                .eventId(dto.getEventId())
                .tariff(dto.getTariff())
                .planType(dto.getPlanType())
                .remainingViews(promotionPlan.getViewsCount())
                .status(status)
                .promotionPayment(promotionPaymentMapper.toEntity(payment))
                .build();
    }

    private PromotionStatus getPromotionStatus(PromotionPaymentStatus status) {
        if (PromotionPaymentStatus.ACCEPTED.equals(status)) {
            return PromotionStatus.ACTIVE;
        } else {
            return PromotionStatus.INACTIVE;
        }
    }
}
