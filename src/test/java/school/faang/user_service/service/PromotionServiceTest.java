package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.payment.CreateOrderDto;
import school.faang.user_service.dto.payment.OrderDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.promotion.BuyPromotionDto;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionPlan;
import school.faang.user_service.entity.promotion.TargetType;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.repository.Promotion.PromotionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PromotionServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private PromotionMapper promotionMapper;

    @Mock
    private UserContext userContext;

    @Mock
    PromotionDto promotionDto;

    @InjectMocks
    private PromotionService promotionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void buyPromotion_shouldThrowExceptionWhenUserDoesNotExist() {
        BuyPromotionDto buyPromotionDto = new BuyPromotionDto();
        buyPromotionDto.setUserId(1L);

        when(userService.isUserExist(1L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> promotionService.buyPromotion(buyPromotionDto));

        assertEquals("Пользователь не существует", exception.getMessage());
    }

    @Test
    void activatePromotion_shouldThrowExceptionIfActive() {
        Long promotionId = 1L;
        Long orderId = 1L;
        Promotion promotion = Promotion.builder()
                .id(promotionId)
                .userId(1L)
                .plan(PromotionPlan.builder().id(1L).impressions(100).cost(BigDecimal.TEN).build())
                .impressionsLimit(100)
                .currentImpressions(0)
                .isActive(false)
                .build();

        OrderDto orderDto = mock(OrderDto.class);
        when(orderDto.getPaymentStatus()).thenReturn(PaymentStatus.ERROR);

        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));
        when(paymentServiceClient.getOrder(orderId)).thenReturn(orderDto);

        PaymentFailedException exception = assertThrows(PaymentFailedException.class,
                () -> promotionService.activatePromotion(orderId, promotionId));

        assertEquals("Оплата не прошла успешно", exception.getMessage());
        verify(promotionRepository, never()).save(any());
    }


    @Test
    void activatePromotion_shouldThrowExceptionIfPaymentFailed() {
        Long promotionId = 1L;
        Long orderId = 1L;
        Promotion promotion = Promotion.builder()
                .id(promotionId)
                .userId(1L)
                .plan(PromotionPlan.builder().id(1L).impressions(100).cost(BigDecimal.TEN).build())
                .impressionsLimit(100)
                .currentImpressions(0)
                .isActive(false)
                .build();

        OrderDto orderDto = mock(OrderDto.class);
        when(orderDto.getPaymentStatus()).thenReturn(PaymentStatus.ERROR);

        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));
        when(paymentServiceClient.getOrder(orderId)).thenReturn(orderDto);

        PaymentFailedException exception = assertThrows(PaymentFailedException.class,
                () -> promotionService.activatePromotion(orderId, promotionId));

        assertEquals("Оплата не прошла успешно", exception.getMessage());
        verify(promotionRepository, never()).save(any());
    }

    @Test
    void activatePromotion_shouldActivatePromotionSuccessfully() {
        Long promotionId = 1L;
        Long orderId = 1L;
        Promotion promotion = Promotion.builder()
                .id(promotionId)
                .userId(1L)
                .impressionsLimit(100)
                .currentImpressions(0)
                .isActive(false)
                .build();

        OrderDto orderDto = mock(OrderDto.class);
        when(orderDto.getPaymentStatus()).thenReturn(PaymentStatus.SUCCESS);

        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));
        when(paymentServiceClient.getOrder(orderId)).thenReturn(orderDto);
        when(promotionMapper.toDto(promotion)).thenReturn(new PromotionDto());

        PromotionDto result = promotionService.activatePromotion(orderId, promotionId);

        assertNotNull(result);
        assertTrue(promotion.isActive());
        verify(promotionRepository).save(promotion);
    }


    @Test
    void getAllPromotionsForUser_shouldReturnPromotionsList() {
        Long userId = 1L;
        List<Promotion> promotions = List.of(Promotion.builder().id(1L).userId(userId).build());
        when(promotionRepository.findActivePromotionsByUserId(userId)).thenReturn(promotions);
        when(promotionMapper.toDto(any(Promotion.class))).thenReturn(new PromotionDto());

        List<PromotionDto> result = promotionService.getAllPromotionsForUser(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(promotionRepository).findActivePromotionsByUserId(userId);
    }

    @Test
    void updatePromotion_shouldUpdatePromotionSuccessfully() {
        Long promotionId = 1L;
        Long userId = 1L;

        BuyPromotionDto buyPromotionDto = BuyPromotionDto.builder()
                .userId(userId)
                .plan(PromotionPlan.builder().id(1L).impressions(200).cost(BigDecimal.TEN).build())
                .build();

        Promotion promotion = Promotion.builder()
                .id(promotionId)
                .userId(userId)
                .isActive(true)
                .plan(PromotionPlan.builder().id(1L).impressions(100).cost(BigDecimal.valueOf(5)).build())
                .currentImpressions(50)
                .impressionsLimit(100)
                .build();

        when(userContext.getUserId()).thenReturn(userId);
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));

        OrderDto mockOrderDto = new OrderDto();
        mockOrderDto.setPaymentStatus(PaymentStatus.SUCCESS);
        when(paymentServiceClient.createOrder(promotionMapper.toCreateOrderDto(buyPromotionDto)))
                .thenReturn(mockOrderDto);
        when(promotionMapper.toDto(promotion)).thenReturn(new PromotionDto());

        PromotionDto result = promotionService.updatePromotion(buyPromotionDto, promotionId);

        assertNotNull(result);
        assertEquals(200, promotion.getImpressionsLimit());
        verify(promotionRepository).save(promotion);
    }


    @Test
    void deletePromotion_shouldDeactivatePromotion() {
        Long promotionId = 1L;
        Promotion promotion = Promotion.builder().id(promotionId).isActive(true).build();

        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));

        promotionService.deletePromotion(promotionId);

        assertFalse(promotion.isActive());
        verify(promotionRepository).save(promotion);
    }

    @Test
    void updatePromotion_shouldThrowExceptionIfPromotionNotActive() {
        Long userId = 1L;
        Long promotionId = 1L;
        BuyPromotionDto buyPromotionDto = BuyPromotionDto.builder().userId(1L).build();

        Promotion promotion = Promotion.builder()
                .id(promotionId)
                .userId(userId)
                .isActive(true)
                .plan(PromotionPlan.builder().id(1L).impressions(50).cost(BigDecimal.valueOf(5)).build())
                .currentImpressions(100)
                .impressionsLimit(50)
                .build();
        when(userContext.getUserId()).thenReturn(userId);

        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> promotionService.updatePromotion(buyPromotionDto, promotionId));

        assertEquals("Промоушен не активен, обновление невозможно", exception.getMessage());
    }

}
