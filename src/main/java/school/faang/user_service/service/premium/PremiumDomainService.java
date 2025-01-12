package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PremiumDomainService {
    private final PremiumRepository premiumRepository;

    @Transactional
    public Premium save(Premium premium) {
        return premiumRepository.save(premium);
    }

    @Transactional(readOnly = true)
    public List<Premium> findAllByEndDateBefore(LocalDateTime endDate) {
        return premiumRepository.findAllByEndDateBefore(endDate);
    }

    @Transactional
    public void deleteAllPremiumsById(List<Premium> premiums) {
        premiumRepository.deleteAllInBatch(premiums);
    }
}
