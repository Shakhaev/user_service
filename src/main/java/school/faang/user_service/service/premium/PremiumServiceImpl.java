package school.faang.user_service.service.premium;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.premium.BoughtPremiumEventDto;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.publisher.BoughtPremiumPublisher;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.user.UserService;

@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {

  private final PremiumRepository premiumRepository;
  private final UserService userService;
  private final PremiumMapper premiumMapper;
  private final BoughtPremiumPublisher boughtPremiumPublisher;
//  private final PaymentServiceClient paymentServiceClient;
//  private final ObjectMapper objectMapper;

  @Override
  public PremiumDto buyPremium(long userId, PremiumPeriod premiumPlan) {
    validatePremiumUser(userId);
    PremiumDto premiumDto = createPremiumDto(userId, premiumPlan);
    validatePayment();
    Premium premium = premiumMapper.toEntity(premiumDto);
    premium.setUser(userService.getUserById(userId));
    premiumDto = premiumMapper.toDto(premiumRepository.save(premium));
    boughtPremiumPublisher.publish(BoughtPremiumEventDto.builder()
        .userId(userId)
        .sum(premiumPlan.getPrice())
        .days(premiumPlan.getDays())
        .receivedAt(LocalDateTime.now().toString())
        .build());
    return premiumDto;
  }

  private void validatePayment() {
//    TODO
  }

  private PremiumDto createPremiumDto(long userId, PremiumPeriod premiumPlan) {
    LocalDateTime startDate = LocalDateTime.now();
    return PremiumDto.builder()
        .userId(userId)
        .startDate(startDate)
        .endDate(startDate.plusDays(premiumPlan.getDays()))
        .build();
  }

  private void validatePremiumUser(long userId) {
    if (premiumRepository.existsByUserId(userId)) {
      throw new DataValidationException("User already has premium subscription!");
    }
  }
}
