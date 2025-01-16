package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.constant.PaymentStatus;
import school.faang.user_service.constant.PremiumPeriod;
import school.faang.user_service.dto.PaymentRequest;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.dto.res.PaymentResponse;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.exception.PremiumBadRequestException;
import school.faang.user_service.exception.PremiumNotFoundException;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.PremiumService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private final PaymentServiceClient paymentServiceClient;
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    private final UserRepository userRepository;

    @Override
    public PremiumDto buyPremium(Long userId, PremiumPeriod premiumPeriod) {
        User user = getUserWithCheck(userId);
        checkIfAlreadyPremium(user);
        PaymentRequest paymentRequest = getPaymentRequest(premiumPeriod);
        PaymentResponse paymentResponse = getNonNullPaymentResponse(paymentRequest);

        if (Objects.requireNonNull(paymentResponse)
                .status()
                .equals(PaymentStatus.SUCCESS)) {
            Premium premium = savePremium(premiumPeriod, user);
            return premiumMapper.toDto(premium);
        }
        throw new PremiumBadRequestException(String.format("Error from paymentService: %s", paymentResponse.message()));
    }

    private User getUserWithCheck(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new PremiumNotFoundException("No user found by this userId"));
    }

    private void checkIfAlreadyPremium(User user) {
        if (premiumRepository.existsByUserId(user.getId())) {
            throw new PremiumBadRequestException("User already has a Premium");
        }
    }

    private static PaymentRequest getPaymentRequest(PremiumPeriod premiumPeriod) {
        return PaymentRequest.builder()
                .paymentNumber(Instant.now()
                        .toEpochMilli())
                .amount(premiumPeriod.getPrice())
                .currency(premiumPeriod.getCurrency())
                .build();
    }

    @NotNull
    private PaymentResponse getNonNullPaymentResponse(PaymentRequest paymentRequest) {
        return Objects.requireNonNull(
                paymentServiceClient.sendPayment(paymentRequest)
                        .getBody(),
                "Payment service returned null response"
        );
    }

    @NotNull
    private Premium savePremium(PremiumPeriod premiumPeriod, User user) {
        return premiumRepository.save(Premium.builder()
                .user(user)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now()
                        .plusDays(premiumPeriod.getDays()))
                .build());
    }
}
