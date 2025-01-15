package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.paymentService.CreateOrderDto;
import school.faang.user_service.dto.premium.Plan;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.premium.PremiumRepository;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final UserContext userContext;

    public String buyPremium(long user_id, Plan plan, String paymentMethod) {
        if (!userRepository.existsById(user_id)) {
            throw new DataValidationException("Такой пользователь не существует");
        }
        if (premiumRepository.existsByUserId(user_id)) {
            throw new DataValidationException("Пользователь уже является премиум пользователем");
        }
        CreateOrderDto dto = new CreateOrderDto(plan.getValue(), paymentMethod);
        userContext.setUserId(user_id);
        return paymentServiceClient.createPayment(dto);
    }
}
