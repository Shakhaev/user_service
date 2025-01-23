package school.faang.user_service.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.request.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.response.RecommendationResponseDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.recommendation_request.*;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private RecommendationRequestValidator validator;
    private List<RecommendationRequestFilter> requestFilters;
    private RecommendationRequestFilterDto filters;
    private RecommendationRequestMapper mapper;
    private RecommendationRequestService recommendationRequestService;

    @BeforeEach
    void setUp() {
        filters = new RecommendationRequestFilterDto();
        requestFilters = new ArrayList<>();
        requestFilters.add(new RecommendationRequestRequesterFilter());
        requestFilters.add(new RecommendationRequestReceiverFilter());
        requestFilters.add(new RecommendationRequestStatusFilter());
        requestFilters.add(new RecommendationRequestSkillFilter());

        mapper = Mappers.getMapper(RecommendationRequestMapper.class);
        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository,
                skillRequestRepository, mapper, validator, requestFilters);
    }

    @Test
    void testCreateRecommendationRequest() {
        RecommendationRequestDto request = new RecommendationRequestDto();
        request.setMessage("Message");
        request.setSkillIds(List.of());
        request.setRequesterId(1L);
        request.setReceiverId(2L);

        when(validator.checkUsersExist(1L, 2L)).thenReturn(true);
        when(validator.checkRequestWithinSixMonthsExist(1L, 2L)).thenReturn(true);
        when(validator.checkAllSkillsExist(List.of())).thenReturn(true);

        when(recommendationRequestRepository.create(1L, 2L, "Message"))
                .thenReturn(RecommendationRequest.builder()
                        .id(1L)
                        .requester(User.builder().id(1L).build())
                        .receiver(User.builder().id(2L).build())
                        .skills(List.of())
                        .message("Message")
                .build());

        RecommendationResponseDto expected = new RecommendationResponseDto();
        expected.setId(1L);
        expected.setMessage("Message");
        expected.setSkillIds(List.of());
        expected.setRequesterId(1L);
        expected.setReceiverId(2L);

        RecommendationResponseDto actual = recommendationRequestService.createRecommendationRequest(request);

        assertEquals(expected, actual);
    }

    @Test
    void testGetRecommendationRequests() {
        filters.setSkillPattern("Java");

        Skill skill1 = new Skill();
        skill1.setTitle("Java");

        SkillRequest skillRequest1 = new SkillRequest();
        skillRequest1.setSkill(skill1);

        RecommendationRequest request1 = new RecommendationRequest();
        request1.setSkills(List.of(skillRequest1));

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1));

        List<RecommendationResponseDto> expected = List.of(mapper.toDto(request1));

        List<RecommendationResponseDto> actual = recommendationRequestService.getRecommendationRequests(filters);

        assertEquals(expected, actual);
    }

    @Test
    void testGetRecommendationRequest() {
        RecommendationResponseDto expected = new RecommendationResponseDto();
        expected.setId(1L);
        expected.setReceiverId(2L);
        expected.setRequesterId(3L);
        expected.setSkillIds(List.of());

        RecommendationRequest request = new RecommendationRequest();
        request.setId(1L);
        request.setReceiver(User.builder().id(2L).build());
        request.setRequester(User.builder().id(3L).build());

        request.setSkills(List.of());

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        RecommendationResponseDto actual = recommendationRequestService.getRecommendationRequest(1L);
        assertEquals(expected, actual);
    }

    @Test
    void testRejectRecommendationRequest() {
        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.PENDING);
        request.setSkills(List.of());

        when(recommendationRequestRepository.findById(1L))
                .thenReturn(Optional.of(request));

        RecommendationResponseDto expected = new RecommendationResponseDto();
        expected.setStatus(RequestStatus.REJECTED);
        expected.setRejectionReason("Rejection reason");
        expected.setSkillIds(List.of());

        RecommendationRejectionDto rejection = new RecommendationRejectionDto("Rejection reason");
        RecommendationResponseDto actual = recommendationRequestService
                .rejectRecommendationRequest(1L, rejection);

        assertEquals(expected, actual);
    }
}