package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.client.payment.PaymentResponse;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.PremiumPeriod;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.validator.PaymentValidator;
import school.faang.user_service.validator.PremiumValidator;
import school.faang.user_service.validator.UserValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumService {
    private final PremiumRepository premiumRepository;
    private final PremiumValidator premiumValidator;
    private final UserService userService;
    private final PaymentValidator paymentValidator;
    private final PremiumMapper premiumMapper;
    private final PaymentService paymentService;
    private final UserValidator userValidator;
    private final PremiumCleanerService premiumCleanerService;

    @Value("${del.batch-size}")
    private int batchSize;

    @Transactional
    public PremiumDto buyPremium(long userId, PremiumPeriod premiumPeriod) {
        userValidator.validateUserById(userId);
        premiumValidator.validateUserIsNotPremium(userId);
        PaymentResponse response = paymentService.sentPayment(premiumPeriod);
        paymentValidator.checkIfPaymentSuccess(response);
        Premium premium = createPremium(userId, premiumPeriod);
        premiumRepository.save(premium);
        return premiumMapper.toDto(premium);
    }

    private Premium createPremium(long userId, PremiumPeriod premiumPeriod) {
        userValidator.validateUserById(userId);
        return Premium.builder()
                .user(userService.findUserById(userId))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(premiumPeriod.getDays()))
                .build();
    }


    public void deleteExpiredPremiums(LocalDateTime time) {
        log.info("Starting premium deletion process for premiums before: {}", time);
        List<Premium> premiumsToDelete = premiumRepository.findAllByEndDateBefore(time);

        if(premiumsToDelete.isEmpty()){
            log.info("No premiums found to delete before: {}", time);
            return;
        }

        List<List<Premium>> partitions = splitPremiumsIntoBatches(premiumsToDelete, batchSize);
        log.info("Found {} premiums to delete, splitting into batches of size {}", premiumsToDelete.size(), batchSize);

        partitions.forEach(premiumCleanerService::deletePremium);
        log.info("Premium deletion process completed.");
    }

    private List<List<Premium>> splitPremiumsIntoBatches(List<Premium> premiumsToDelete, int size) {
        List<List<Premium>> batches = new ArrayList<>();
        for (int i = 0; i < premiumsToDelete.size(); i += size) {
            batches.add(new ArrayList<>(premiumsToDelete.subList(i, Math.min(premiumsToDelete.size(), i + size))));
        }
        return batches;
    }
}
