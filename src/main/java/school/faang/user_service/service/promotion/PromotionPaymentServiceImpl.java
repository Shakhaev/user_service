package school.faang.user_service.service.promotion;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.promotion.PromotionPaymentDto;
import school.faang.user_service.entity.promotion.PromotionPayment;
import school.faang.user_service.mapper.promotion.PromotionPaymentMapper;
import school.faang.user_service.repository.promotion.PromotionPaymentRepository;

@Service
@RequiredArgsConstructor
public class PromotionPaymentServiceImpl implements PromotionPaymentService {
    private final PromotionPaymentRepository promotionPaymentRepository;
    private final PromotionPaymentMapper promotionPaymentMapper;

    @Transactional
    @Override
    public PromotionPayment create(PromotionPayment promotionPayment) {
        return promotionPaymentRepository.save(promotionPayment);
    }

    @Override
    public PromotionPaymentDto getPromotionPaymentById(String id) {
        return promotionPaymentMapper.toDto(promotionPaymentRepository.findPromotionPaymentById(id));
    }
}
