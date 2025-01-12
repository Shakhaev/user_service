package school.faang.user_service.service.promotion.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.payment.PaymentStatus;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;
import school.faang.user_service.exception.payment.UnsuccessfulUserPromotionPaymentException;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.promotion.PromotionTaskService;
import school.faang.user_service.service.promotion.util.UserPromoBuilder;
import school.faang.user_service.service.user.UserDomainService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserPromotionService {
    private final UserPromoValidationService userPromoValidationService;
    private final UserPromotionRepository userPromotionRepository;
    private final PromotionTaskService promotionTaskService;
    private final UserDomainService userDomainService;
    private final UserPromoBuilder userPromoBuilder;
    private final PaymentService paymentService;

    @Transactional
    public UserPromotion buyPromotion(long userId, PromotionTariff tariff) {
        log.info("User with id: {} buy promotion tariff: {}", userId, tariff.toString());
        User user = userDomainService.findById(userId);
        userPromoValidationService.checkUserForPromotion(user);

        PaymentResponseDto paymentResponse = paymentService.sendPayment(tariff);
        if (!paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
            throw new UnsuccessfulUserPromotionPaymentException(tariff.getNumberOfViews(), userId, paymentResponse.message());
        }

        UserPromotion promotion = userPromoBuilder.buildUserPromotion(user, tariff);
        return userPromotionRepository.save(promotion);
    }

    @Transactional(readOnly = true)
    public List<User> getPromotedUsersBeforeAllPerPage(Long offset, Long limit) {
        log.info("Get promoted users before all per page: {} - {}", offset, limit);
        List<User> users = userDomainService.findAllSortedByPromotedUsersPerPage(offset, limit);
        List<UserPromotion> activeUserPromotions = userPromoValidationService.getActiveUserPromotions(users);

        if (!activeUserPromotions.isEmpty()) {
            promotionTaskService.incrementUserPromotionViews(activeUserPromotions);
        }
        return users;
    }
}
