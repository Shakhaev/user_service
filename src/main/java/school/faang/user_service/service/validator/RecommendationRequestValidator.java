package school.faang.user_service.service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.InvalidMessageException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;

    public void validate(RecommendationRequestDto requestDto) {
        validateMessage(requestDto.getMessage());
        validateUserExist(requestDto.getRequesterId(), requestDto.getReceiverId());
        validateSkillsExist(requestDto);
        checkRequestTime(requestDto.getRequesterId(), requestDto.getReceiverId());
    }

    private void validateMessage(String msg) {
        if (msg == null || msg.trim().isEmpty()) {
            throw new InvalidMessageException("Сообщение не может быть пустое.");
        }
    }

    private void validateUserExist(Long requesterId, Long receiverId) {
        if (!userRepository.existsById(requesterId)) {
            throw new NoSuchElementException("Запрашивающий пользователь не найден.");
        }
        if (!userRepository.existsById(receiverId)) {
            throw new NoSuchElementException("Получающий пользователь не найден.");
        }
    }

    private void validateSkillsExist(RecommendationRequestDto requestDto) {
        requestDto.getSkills()
                .forEach(skill -> {
                    if (!skillRepository.existsById(skill.getId())){
                        throw new NoSuchElementException("Скил не существует: " + skill.getId());
                    }
                });
    }

    private void checkRequestTime(Long requesterId, Long receiverId) {
        Optional<RecommendationRequest> recommendationRequestOptional =
                recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);

        if (recommendationRequestOptional.isEmpty() ||
                    recommendationRequestOptional.get().getCreatedAt().isBefore(LocalDateTime.now().minusMonths(6))) {
            throw new NoSuchElementException(
                    "Для указанного запрашивающего и получающего не " +
                            "найдено ожидающих рассмотрения запросов на рекомендации");
        }
    }
}
