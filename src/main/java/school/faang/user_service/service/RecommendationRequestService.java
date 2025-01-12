package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestService {

    private static final int SIX_MONTH_RECOMMENDATION_LIMIT = 6;

    private final RecommendationRequestRepository requestRepository;

    private final RecommendationRequestMapper recommendationRequestMapper;

    private final UserRepository userRepository;

    private final SkillRepository skillRepository;

    private final SkillRequestRepository skillRequestRepository;

    private final List<RequestFilter> requestFilters;

    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        log.info("Создание запроса на рекомендацию: {}", dto);

        List<Skill> skills = validateRequest(dto);

        RecommendationRequest request = recommendationRequestMapper.toEntity(dto);
        request = requestRepository.save(request);

        saveSkillRequests(request, skills);
        log.info("Запрос рекомендации успешно создан с использованием ID: {}", request.getId());

        return recommendationRequestMapper.toDto(request);
    }

    private void saveSkillRequests(RecommendationRequest request, List<Skill> skills) {

        for (Skill skill : skills) {
            SkillRequest skillRequest = new SkillRequest();
            skillRequest.setRequest(request);
            skillRequest.setSkill(skill);
            skillRequestRepository.save(skillRequest);
        }
    }

    private List<Skill> validateRequest(RecommendationRequestDto dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Recommendation request cannot be null.");
        }

        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            log.warn("Ошибка проверки: сообщение пустое или null. DTO: {}", dto);
            throw new IllegalArgumentException("Message cannot be empty");
        }

        if (!userRepository.existsById(dto.getRequesterId()) || !userRepository.existsById(dto.getReceiverId())) {
            log.error("Ошибка проверки: Один или два юзера не существуют. RequesterId: {}, ReceiverId: {}",
                    dto.getRequesterId(), dto.getReceiverId());
            throw new IllegalArgumentException("Users must exist");
        }

        if (requestRepository.findLatestPendingRequest(dto.getRequesterId(), dto.getReceiverId())
                .filter(request -> request.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(SIX_MONTH_RECOMMENDATION_LIMIT)))
                .isPresent()) {
            log.warn("Запрос уже существует в течение последних 6 месяцев DTO: {}", dto);
            throw new IllegalArgumentException("Request already exists within the past 6 months");
        }

        List<Long> skillIds = dto.getSkillsIds();
        if (skillIds == null) {
            log.warn("Ошибка проверки: В запросе не указаны навыки. DTO: {}", dto);
            return List.of();
        }

        List<Skill> skills = skillRepository.findAllById(skillIds);

        if (skills.size() != skillIds.size()) {
            log.warn("Некоторые навыки не существуют. Предоставленные ID навыков: {}", skillIds);
            throw new IllegalArgumentException("One or more skills do not exist");
        }
        return skills;
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        log.info("Отклонение запроса на рекомендацию ID: {}", id);
        RecommendationRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            log.error("Запрос на рекомендацию с ID {} не найден", id);
            throw new IllegalStateException("Cannot reject a non-pending request");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        request = requestRepository.save(request);
        log.info("Запрос на рекомендацию с ID: {} откланен успешно", id);
        return recommendationRequestMapper.toDto(request);
    }

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {

        validateRequest(recommendationRequest);
        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());

        return create(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        log.info("Получение запросов на рекомендации с помощью фильтра: {}", filter);
        List<RecommendationRequest> requests = requestRepository.findAll();

        List<RecommendationRequestDto> result = requests.stream()
                .filter(request -> filterMatches(request, filter))
                .map(recommendationRequestMapper::toDto)
                .toList();

        log.info("Фильтр для сопоставления запросов на рекомендации: {}", filter);
        return result;
    }

    public RecommendationRequestDto getRecommendationRequests(long id) {
        RecommendationRequest request = findRequestById(id);
        return recommendationRequestMapper.toDto(request);
    }

    private RecommendationRequest findRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("запрос на рекомендацию с ID {} не найден", id);
                    return new EntityNotFoundException("RecommendationRequest not found");
                });
    }

    private boolean filterMatches(RecommendationRequest request, RequestFilterDto filters) {
        Stream<RecommendationRequest> filteredStream = Stream.of(request);

        for (RequestFilter filter : requestFilters) {
            if (filter.isApplicable(filters)) {
                filteredStream = filter.apply(filteredStream, filters);
            }
        }
        return filteredStream.findAny().isPresent();
    }
}