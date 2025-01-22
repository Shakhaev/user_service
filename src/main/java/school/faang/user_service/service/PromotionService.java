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

        List<Promotion> activePromotions = promotionRepository.findActivePromotionsByUserId(buyPromotionDto.getUserId());
        if (activePromotions.stream().anyMatch(promotion -> promotion.getPlan().equals(buyPromotionDto.getPlan()))) {
            throw new BusinessException("У пользователя уже есть промоакция с таким же планом");
        }

        CreateOrderDto createOrderDto = promotionMapper.toCreateOrderDto(buyPromotionDto);

        Promotion promotion = Promotion.builder()
                .userId(buyPromotionDto.getUserId())
                .plan(buyPromotionDto.getPlan())
                .impressionsLimit(buyPromotionDto.getPlan().getImpressions())
                .currentImpressions(0)
                .isActive(false)
                .startTime(null)
                .build();
        promotionRepository.save(promotion);

        return paymentServiceClient.createOrder(createOrderDto);
    }

    @Transactional
    public PromotionDto activatePromotion(long orderId, Long promotionId) {
        userContext.setUserId(0);
        OrderDto orderDto = paymentServiceClient.getOrder(orderId);
        if (!orderDto.getPaymentStatus().equals(PaymentStatus.SUCCESS)) {
            throw new PaymentFailedException("Заказ не оплачен");
        }
        if (!orderDto.getServiceType().equalsIgnoreCase("promotion")) {
            throw new BusinessException("Тип услуги не корректен");
        }
        Promotion promotion = findPromotion(promotionId);

        promotion.setActive(true);
        promotion.activate();

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
            throw new BusinessException("Промоушен не активен, обновление невозможно");
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
