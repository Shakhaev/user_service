package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RecommendationRequestCreatedException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.SkillService;
import school.faang.user_service.service.recommendation.UserService;
import school.faang.user_service.service.recommendation.impl.RecommendationRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Spy
    private RecommendationRequestMapper recommendationRequestMapper;
    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;
    @Captor
    private ArgumentCaptor<RecommendationRequest> recommendationRequestCaptor;

    private RecommendationRequest recommendationRequest;
    private User user;
    private Skill skill;
    private RecommendationRequestDto recommendationRequestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        skill = new Skill();
        recommendationRequest = new RecommendationRequest();
    }

    @Test
    void testCreateWithNotExistingRequester() {
        recommendationRequestDto = new RecommendationRequestDto("Hello",
                RequestStatus.PENDING, List.of(1L, 2L), 1L, 2L);
        Mockito.when(userService.findById(recommendationRequestDto.requesterId()))
                .thenThrow(ResourceNotFoundException
                        .userNotFoundException(recommendationRequestDto.requesterId()));
        Assert.assertThrows(ResourceNotFoundException.class,
                () -> recommendationRequestService.create(recommendationRequestDto));
    }

    @Test
    void testCreateWithNotExistingReceiver() {
        recommendationRequestDto = new RecommendationRequestDto("Hello",
                RequestStatus.PENDING, List.of(1L, 2L), 1L, 2L);
        Mockito.when(userService.findById(recommendationRequestDto.requesterId()))
                .thenReturn(user);
        Mockito.when(userService.findById(recommendationRequestDto.receiverId()))
                .thenThrow(ResourceNotFoundException
                        .userNotFoundException(recommendationRequestDto.receiverId()));
        Assert.assertThrows(ResourceNotFoundException.class,
                () -> recommendationRequestService.create(recommendationRequestDto));
    }

    @Test
    void testCreateWithSixMonthLimit() {
        recommendationRequestDto = new RecommendationRequestDto("Hello",
                RequestStatus.PENDING, List.of(1L, 2L), 1L, 2L);
        Mockito.when(userService.findById(recommendationRequestDto.requesterId()))
                .thenReturn(user);
        Mockito.when(userService.findById(recommendationRequestDto.receiverId()))
                .thenReturn(user);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(recommendationRequestDto.requesterId(),
                        recommendationRequestDto.receiverId()))
                .thenReturn(Optional.of(recommendationRequest));
        Assert.assertThrows(RecommendationRequestCreatedException.class,
                () -> recommendationRequestService.create(recommendationRequestDto));
    }

    @Test
    void testCreateWithNotExistingSkill() {
        recommendationRequestDto = new RecommendationRequestDto("Hello",
                RequestStatus.PENDING, List.of(1L, 2L), 1L, 2L);
        Mockito.when(userService.findById(recommendationRequestDto.requesterId()))
                .thenReturn(user);
        Mockito.when(userService.findById(recommendationRequestDto.receiverId()))
                .thenReturn(user);
        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(recommendationRequestDto.requesterId(),
                        recommendationRequestDto.receiverId())).thenReturn(Optional.empty());
        Mockito.when(skillService.findById(recommendationRequestDto.skills()
                .get(0)))
                .thenThrow(ResourceNotFoundException.class);
        Assert.assertThrows(ResourceNotFoundException.class,
                () -> recommendationRequestService.create(recommendationRequestDto));
    }

    @Test
    void testCreateSaveRecommendationRequest() {
        recommendationRequestDto = new RecommendationRequestDto("Hello",
                RequestStatus.PENDING, List.of(1L, 2L), 1L, 2L);
        Mockito.when(userService.findById(recommendationRequestDto.requesterId()))
                .thenReturn(user);
        Mockito.when(userService.findById(recommendationRequestDto.receiverId()))
                .thenReturn(user);
        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(recommendationRequestDto.requesterId(),
                recommendationRequestDto.receiverId())).thenReturn(Optional.empty());
        Mockito.when(skillService.findById(1L)).thenReturn(skill);
        Mockito.when(skillService.findById(2L)).thenReturn(skill);
        Mockito.verify(recommendationRequestRepository, Mockito.times(1)).save(recommendationRequestCaptor.capture());
    }
}
