package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;
    private final UserRepository userRepository;
    private final PaymentServiceClient paymentServiceClient;

    @Override
    public PremiumDto buyPremium(Long userId, PremiumPeriod premiumPeriod) {
        User user = validateAndGetUser(userId);
        PaymentRequest paymentRequest = createPaymentRequest(premiumPeriod);
        PaymentResponse paymentResponse = getNonNullPaymentResponse(paymentRequest);

        if (paymentResponse.status()
                .equals(PaymentStatus.SUCCESS)) {
            Premium premium = savePremium(premiumPeriod, user);
            return premiumMapper.toDto(premium);
        }
        throw new PremiumBadRequestException(String.format("Error from paymentService: %s", paymentResponse.message()));
    }

    private User validateAndGetUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PremiumNotFoundException("No user found by this userId"));
        if (premiumRepository.existsByUserId(user.getId())) {
            throw new PremiumBadRequestException("User already has a Premium");
        }
        return user;
    }

    private static PaymentRequest createPaymentRequest(PremiumPeriod premiumPeriod) {
        return PaymentRequest.builder()
                .paymentNumber(Instant.now()
                        .toEpochMilli())
                .amount(premiumPeriod.getPrice())
                .currency(premiumPeriod.getCurrency())
                .build();
    }

    private PaymentResponse getNonNullPaymentResponse(PaymentRequest paymentRequest) {
        ResponseEntity<PaymentResponse> response = paymentServiceClient.sendPayment(paymentRequest);
        if (response == null || response.getBody() == null) {
            throw new PremiumBadRequestException("Payment service returned null response");
        }
        return response.getBody();
    }

    private Premium savePremium(PremiumPeriod premiumPeriod, User user) {
        LocalDateTime now = LocalDateTime.now();
        return premiumRepository.save(Premium.builder()
                .user(user)
                .startDate(now)
                .endDate(now.plusDays(premiumPeriod.getDays()))
                .build());
    }
}
