package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.payment.CreateOrderDto;
import school.faang.user_service.dto.payment.OrderDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.BuyPromotionDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.Promotion.PromotionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final UserService userService;
    private final PromotionRepository promotionRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final PromotionMapper promotionMapper;
    private final UserContext userContext;

    @Transactional
    public OrderDto buyPromotion(BuyPromotionDto buyPromotionDto) {
        userContext.setUserId(buyPromotionDto.getUserId());
        if (!userService.isUserExist(buyPromotionDto.getUserId())) {
            throw new EntityNotFoundException("Пользователь не существует");
        }

        CreateOrderDto createOrderDto = promotionMapper.toCreateOrderDto(buyPromotionDto);

        OrderDto orderDto = paymentServiceClient.createOrder(createOrderDto);

        checkPaymentStatus(orderDto);

        return orderDto;
    }

    @Transactional
    public PromotionDto activatePromotion(Long promotionId, BuyPromotionDto buyPromotionDto) {
        Promotion promotion = findPromotion(promotionId);

        if (promotion.isActive()) {
            throw new IllegalStateException("Промо-акция уже активирована");
        }

        PromotionPlan plan = buyPromotionDto.getPlan();
        promotion.setPlan(plan);
        promotion.setImpressionsLimit(plan.getImpressions());
        promotion.setActive(true);

        promotionRepository.save(promotion);

        return promotionMapper.toDto(promotion);
    }

    public List<PromotionDto> getAllPromotionsForUser(Long userId) {
        userContext.setUserId(userId);
        List<Promotion> promotions = promotionRepository.findActivePromotionsByUserId(userId);
        return promotions.stream()
                .map(promotionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PromotionDto updatePromotion(BuyPromotionDto buyPromotionDto, Long promotionId) {
        userContext.setUserId(buyPromotionDto.getUserId());
        Promotion promotion = findPromotion(promotionId);

        checkOwnership(userContext.getUserId(), promotion);

        if (!promotion.isActive()) {
            throw new IllegalStateException("Промоушен не активен, обновление невозможно");
        }

        PromotionPlan currentPlan = promotion.getPlan();
        PromotionPlan newPlan = buyPromotionDto.getPlan();

        int additionalImpressions = newPlan.getImpressions() - currentPlan.getImpressions();

        if (additionalImpressions > 0) {
            double additionalCost = calculateAdditionalCost(additionalImpressions, newPlan);
            CreateOrderDto createOrderDto = promotionMapper.toCreateOrderDto(buyPromotionDto);
            OrderDto orderDto = paymentServiceClient.createOrder(createOrderDto);
            checkPaymentStatus(orderDto);
        }

        promotion.setPlan(newPlan);
        promotion.setImpressionsLimit(newPlan.getImpressions());

        promotionRepository.save(promotion);

        return promotionMapper.toDto(promotion);
    }

    public void deletePromotion(Long id) {
        Promotion promotion = findPromotion(id);
        promotion.setActive(false);
        promotionRepository.save(promotion);
    }

    private void checkPaymentStatus(OrderDto orderDto) {
        if (orderDto == null || orderDto.getPaymentStatus() == null) {
            throw new PaymentFailedException("Некорректный ответ от платежного сервиса");
        }
        if (!orderDto.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
            throw new PaymentFailedException("Оплата не прошла");
        }
    }

    private Promotion findPromotion(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promotion не найден"));
    }

    private void checkOwnership(Long userId, Promotion promotion) {
        if (!promotion.getUserId().equals(userId)) {
            throw new BusinessException("Пользователь не имеет прав на эту промоакцию");
        }
    }

    private double calculateAdditionalCost(int additionalImpressions, PromotionPlan newPlan) {
        BigDecimal costPerImpression = newPlan.getCost()
                .divide(BigDecimal.valueOf(newPlan.getImpressions()), RoundingMode.HALF_UP);

        return costPerImpression.multiply(BigDecimal.valueOf(additionalImpressions)).doubleValue();
    }

}
