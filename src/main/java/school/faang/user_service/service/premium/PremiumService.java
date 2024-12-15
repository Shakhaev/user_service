package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.premium.PremiumBoughtEvent;
import school.faang.user_service.dto.premium.ResponsePremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.publisher.premium.PremiumBoughtEventPublisher;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.validator.premium.PremiumValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PaymentService paymentService;
    private final PremiumMapper premiumMapper;
    private final PremiumValidator premiumValidator;
    private final UserValidator userValidator;
    private final PremiumBuilder premiumBuilder;
    private final PremiumBoughtEventPublisher publisher;

    @Transactional
    public ResponsePremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        log.info("Attempting to purchase premium for userId: {} with premiumPeriod: {}", userId, premiumPeriod);

        User user = userValidator.validateUser(userId);
        log.debug("User retrieved: {}", user);

        premiumValidator.validateUserForSubPeriod(user);
        PaymentResponseDto paymentResponse = paymentService.sendPayment(premiumPeriod);
        log.info("Payment sent for userId: {} with amount: {}", userId, premiumPeriod.getCost());

        premiumValidator.checkPaymentResponse(paymentResponse, userId, premiumPeriod);
        Premium premium = premiumBuilder.buildPremium(user, premiumPeriod);

        premium = premiumRepository.save(premium);
        log.debug("Premium saved for userId: {} with paymentNumber: {}", userId, paymentResponse.getPaymentNumber());

        publishEvent(premium, paymentResponse, premiumPeriod);
        return premiumMapper.toDto(premium);
    }

    @Transactional(readOnly = true)
    public List<Premium> findAllByEndDateBefore(LocalDateTime endDate) {
        return premiumRepository.findAllByEndDateBefore(endDate);
    }

    @Transactional
    public void deleteAllPremiumsById(List<Premium> premiums) {
        log.info("Delete all premiums");
        premiumRepository.deleteAllInBatch(premiums);
    }

    private void publishEvent(Premium premium, PaymentResponseDto paymentResponse, PremiumPeriod period) {
        PremiumBoughtEvent event = PremiumBoughtEvent.builder()
                .userId(premium.getUser().getId())
                .amount(paymentResponse.getAmount())
                .period(period.getDays())
                .purchaseDate(now())
                .build();

        publisher.publish(event);
    }
}
