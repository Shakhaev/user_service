package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.leaderboard.LeaderboardDto;
import school.faang.user_service.calculator.leaderboard.LeaderboardCalculator;
import school.faang.user_service.mapper.LeaderboardMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private static final int LEADERBOARD_MAX_SIZE = 15;
    private final LeaderboardMapper userScoreMapper;
    private final UserRepository userRepository;
    private final List<LeaderboardCalculator> scoreCalculators;

    public List<LeaderboardDto> getUsersLeaderboard() {
        return getLeaderboardsDto().parallelStream()
                .map(userDto -> {
                    int totalActivityScore = scoreCalculators.stream()
                            .mapToInt(calculator -> calculator.getScore(userDto))
                            .sum();
                    userDto.setActivityScore(totalActivityScore);
                    return userDto;
                })
                .sorted(Comparator.comparing(LeaderboardDto::getActivityScore).reversed())
                .limit(LEADERBOARD_MAX_SIZE)
                .toList();
    }

    private List<LeaderboardDto> getLeaderboardsDto() {
        return userRepository.findAll().stream()
                .map(userScoreMapper::toDto)
                .toList();
    }
}
