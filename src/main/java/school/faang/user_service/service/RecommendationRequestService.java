package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RequestAlreadyProcessedException;
import school.faang.user_service.exception.RequestSaveException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.validator.RecommendationRequestFilter;
import school.faang.user_service.service.validator.RecommendationRequestValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestValidator validator;
    private final RecommendationRequestFilter recommendationRequestFilter;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto requestDto) {
        //валидация
        validator.validate(requestDto);

        User requester = userRepository.findById(requestDto.getRequesterId())
                .orElseThrow(() -> new UserNotFoundException("Запрашивающий пользователь не найден"));
        User receiver = userRepository.findById(requestDto.getReceiverId())
                .orElseThrow(() -> new UserNotFoundException("Получающий пользователь не найден"));

        //создаем сущность и запрос
        RecommendationRequest entity = recommendationRequestMapper.toEntity(requestDto);
        entity.setRequester(requester);
        entity.setReceiver(receiver);
        try {
            entity = recommendationRequestRepository.save(entity);
        } catch (Exception e) {
            throw new RequestSaveException("Ошибка при сохранении запроса рекомендации", e);
        }

        //добавляем скилы в запрос
        RecommendationRequest finalEntity = entity;
        requestDto.getSkills().forEach(skill -> skillRequestRepository.create(finalEntity.getId(), skill.getId()));

        return recommendationRequestMapper.toDto(entity);
    }

    // Получение списка всех запросов с фильтром
    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        List<RecommendationRequest> allRequests = recommendationRequestRepository.findAll();

        List<RecommendationRequest> filteredRequests = recommendationRequestFilter.filterRequests(allRequests, filter);

        return filteredRequests.stream()
                .map(recommendationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    //отклонение запроса
    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest entity = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Запрос с " + id + " не найден"));

        return recommendationRequestMapper.toDto(entity);
    }

    @Transactional
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Запрос не найден"));

        if (request.getStatus() == RequestStatus.REJECTED || request.getStatus() == RequestStatus.ACCEPTED) {
            throw new RequestAlreadyProcessedException("Запрос был уже обработан");
        }

        //установка статуса отклонения и сохранение изменений
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejectionDto.getRejectionReason());
        recommendationRequestRepository.save(request);

        return recommendationRequestMapper.toDto(request);
    }
}
