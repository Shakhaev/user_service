package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;
import school.faang.user_service.constant.PremiumPeriod;
import school.faang.user_service.dto.PaymentRequest;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.PremiumService;
import school.faang.user_service.service.client.PaymentServiceClient;

@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    @AutoConfigureOrder
    private PaymentServiceClient paymentServiceClient;
    private final PremiumRepository premiumRepository;
    private final PremiumMapper premiumMapper;

    @Override
    public PremiumDto buyPremium(Long userId, PremiumPeriod premiumPeriod) {
        return paymentServiceClient.sendPayment(PaymentRequest);
    }
}
