package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestSaveDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.RecommendationRequestCreatedException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.SkillService;
import school.faang.user_service.service.recommendation.UserService;
import school.faang.user_service.service.recommendation.impl.RecommendationRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;
    @Captor
    private ArgumentCaptor<RecommendationRequest> recommendationRequestCaptor;

    private RecommendationRequest recommendationRequest;
    private User user;
    private Skill skill;
    private RecommendationRequestSaveDto recommendationRequestSaveDto;
    private SkillRequest skillRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        skill = new Skill();
        recommendationRequest = new RecommendationRequest();
        skillRequest = new SkillRequest();
    }

    @Test
    void testCreateWithNotExistingRequester() {
        recommendationRequestSaveDto = new RecommendationRequestSaveDto("Hello", new ArrayList<>(List.of(1L, 2L)), 1L, 2L);
        Mockito.when(userService.findById(recommendationRequestSaveDto.requesterId()))
                .thenThrow(ResourceNotFoundException
                        .userNotFoundException(recommendationRequestSaveDto.requesterId()));
        Assert.assertThrows(ResourceNotFoundException.class,
                () -> recommendationRequestService.create(recommendationRequestSaveDto));
    }

    @Test
    void testCreateWithNotExistingReceiver() {
        recommendationRequestSaveDto = new RecommendationRequestSaveDto("Hello", new ArrayList<>(List.of(1L, 2L)), 1L, 2L);
        Mockito.when(userService.findById(recommendationRequestSaveDto.requesterId()))
                .thenReturn(user);
        Mockito.when(userService.findById(recommendationRequestSaveDto.receiverId()))
                .thenThrow(ResourceNotFoundException
                        .userNotFoundException(recommendationRequestSaveDto.receiverId()));
        Assert.assertThrows(ResourceNotFoundException.class,
                () -> recommendationRequestService.create(recommendationRequestSaveDto));
    }

    @Test
    void testCreateWithSixMonthLimit() {
        recommendationRequestSaveDto = new RecommendationRequestSaveDto("Hello", new ArrayList<>(List.of(1L, 2L)), 1L, 2L);
        Mockito.when(userService.findById(recommendationRequestSaveDto.requesterId()))
                .thenReturn(user);
        Mockito.when(userService.findById(recommendationRequestSaveDto.receiverId()))
                .thenReturn(user);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(recommendationRequestSaveDto.requesterId(),
                        recommendationRequestSaveDto.receiverId()))
                .thenReturn(Optional.of(recommendationRequest));
        Assert.assertThrows(RecommendationRequestCreatedException.class,
                () -> recommendationRequestService.create(recommendationRequestSaveDto));
    }

    @Test
    void testCreateWithNotExistingSkill() {
        recommendationRequestSaveDto = new RecommendationRequestSaveDto("Hello", new ArrayList<>(List.of(1L, 2L)), 1L, 2L);
        Mockito.when(userService.findById(recommendationRequestSaveDto.requesterId()))
                .thenReturn(user);
        Mockito.when(userService.findById(recommendationRequestSaveDto.receiverId()))
                .thenReturn(user);
        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(recommendationRequestSaveDto.requesterId(),
                        recommendationRequestSaveDto.receiverId())).thenReturn(Optional.empty());
        Mockito.when(skillService.findById(recommendationRequestSaveDto.skills()
                .get(0)))
                .thenThrow(ResourceNotFoundException.class);
        Assert.assertThrows(ResourceNotFoundException.class,
                () -> recommendationRequestService.create(recommendationRequestSaveDto));
    }

    @Test
    void testCreateSaveRecommendationRequest() {
        recommendationRequestSaveDto = new RecommendationRequestSaveDto("Hello", new ArrayList<>(List.of(1L, 2L)), 1L, 2L);
        Mockito.when(userService.findById(recommendationRequestSaveDto.requesterId()))
                .thenReturn(user);
        Mockito.when(userService.findById(recommendationRequestSaveDto.receiverId()))
                .thenReturn(user);
        Mockito.when(recommendationRequestRepository.findLatestPendingRequest(recommendationRequestSaveDto.requesterId(),
                recommendationRequestSaveDto.receiverId())).thenReturn(Optional.empty());
        Mockito.when(skillService.findById(1L)).thenReturn(skill);
        Mockito.when(skillService.findById(2L)).thenReturn(skill);
        Mockito.when(recommendationRequestRepository.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(skillRequestRepository.create(Mockito.anyLong(), Mockito.anyLong())).thenReturn(skillRequest);
        RecommendationRequestDto result = recommendationRequestService.create(recommendationRequestSaveDto);
        Mockito.verify(recommendationRequestRepository).save(recommendationRequestCaptor.capture());
        Assert.assertNotNull(result);
        Mockito.verify(skillRequestRepository, Mockito.times(2)).create(Mockito.anyLong(), Mockito.anyLong());
    }
}
