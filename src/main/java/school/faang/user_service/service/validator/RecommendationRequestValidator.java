package school.faang.user_service.service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.exception.InvalidMessageException;
import school.faang.user_service.exception.SkillNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public void validate(RecommendationRequestDto requestDto) {
        validateMessage(requestDto.getMessage());
        validateUserExist(requestDto.getRequesterId(), requestDto.getReceiverId());
        validateSkillsExist(requestDto);
    }

    private void validateMessage(String msg) {
        if (msg == null || msg.trim().isEmpty()) {
            throw new InvalidMessageException("Сообщение не может быть пустое.");
        }
    }

    private void validateUserExist(Long requesterId, Long receiverId) {
        if (!userRepository.existsById(requesterId)) {
            throw new UserNotFoundException("Запрашивающий пользователь не найден.");
        }
        if (!userRepository.existsById(receiverId)) {
            throw new UserNotFoundException("Получающий пользователь не найден.");
        }
    }

    private void validateSkillsExist(RecommendationRequestDto requestDto) {
        requestDto.getSkills()
                .forEach(skill -> {
                    if (!skillRepository.existsById(skill.getId())){
                        throw new SkillNotFoundException("Скил не существует: " + skill.getId());
                    }
                });
    }
}
