package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.premium.PaymentRequest;
import school.faang.user_service.dto.premium.PaymentResponse;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.enums.PaymentStatus;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.exception.PaymentPayException;
import school.faang.user_service.exception.PaymentServiceException;
import school.faang.user_service.exception.PremiumAlreadyExistsException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PremiumService {

    private final static String CURRENCY = "USD";
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final UserService userService;
    private final PremiumMapper premiumMapper;

    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        if (premiumRepository.existsByUserId(userId)) {
            throw new PremiumAlreadyExistsException("The user is already available in the premium");
        }

        var user = userService.getUserById(userId);
        int paymentNumber = new Random().nextInt(1000, 10000);

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .currency(Currency.getInstance(CURRENCY))
                .paymentNumber(paymentNumber)
                .amount(premiumPeriod.getPrice())
                .build();

        try {
            PaymentResponse paymentResponse = paymentServiceClient.processPayment(paymentRequest);
            if (paymentResponse.status() != PaymentStatus.SUCCESS) {
                throw new PaymentPayException("Payment failed.");
            }

            Premium premium = Premium.builder()
                    .user(user)
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now().plusDays(premiumPeriod.getDays()))
                    .build();

            premiumRepository.save(premium);
            return premiumMapper.toDto(premium);
        } catch (PaymentServiceException e) {
            throw new PaymentServiceException("Payment service not working.");
        }
    }
}
