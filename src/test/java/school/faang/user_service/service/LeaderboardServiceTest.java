package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.calculator.leaderboard.LeaderboardCalculator;
import school.faang.user_service.mapper.UserScoreMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LeaderboardServiceTest {

    @Spy
    private UserScoreMapperImpl userScoreMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<LeaderboardCalculator> scoreCalculators;

    @InjectMocks
    private LeaderboardService scoreService;

    private User user1;
    private User user2;

    @BeforeEach
    public void beforeEach() {
        user1 = User.builder()
                .id(1L)
                .experience(100)
                .ownedEvents(List.of(Event.builder().id(1L).build()))
                .mentees((List.of(User.builder().id(2L).build())))
                .goals((List.of(Goal.builder().id(1L).build())))
                .skills((List.of(Skill.builder().id(1L).build())))
                .participatedEvents((List.of(Event.builder().id(1L).build())))
                .recommendationsReceived((List.of(Recommendation.builder().id(1L).build())))
                .build();

        user2 = User.builder()
                .id(2L)
                .experience(150)
                .ownedEvents(List.of(Event.builder().id(1L).build()))
                .mentees(Collections.EMPTY_LIST)
                .goals((List.of(Goal.builder().id(1L).build())))
                .skills((List.of(Skill.builder().id(1L).build())))
                .participatedEvents(Collections.EMPTY_LIST)
                .recommendationsReceived((List.of(Recommendation.builder().id(1L).build())))
                .build();
    }

    @Test
    public void testGetUsersLeaderboard() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<LeaderboardDto> leaderboard = scoreService.getUsersLeaderboard();

        assertEquals(2, leaderboard.size());
    }
}
