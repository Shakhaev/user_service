package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserScoreDto;
import school.faang.user_service.leaderboard.ScoreCalculator;
import school.faang.user_service.mapper.UserScoreMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private static final int LEADERBOARD_MAX_SIZE = 15;
    private final UserScoreMapper userScoreMapper;
    private final UserRepository userRepository;
    private final List<ScoreCalculator> scoreCalculators;

    public List<UserScoreDto> getUsersLeaderboard() {
        List<UserScoreDto> usersDto = userRepository.findAll().stream()
                .map(userScoreMapper::toDto)
                .toList();

        return usersDto.parallelStream()
                .map(userDto -> {
                    int totalActivityScore = scoreCalculators.stream()
                            .mapToInt(calculator -> calculator.getScore(userDto))
                            .sum();
                    userDto.setActivityScore(totalActivityScore);
                    return userDto;
                })
                .sorted(Comparator.comparing(UserScoreDto::getActivityScore).reversed())
                .limit(LEADERBOARD_MAX_SIZE)
                .toList();
    }
}
