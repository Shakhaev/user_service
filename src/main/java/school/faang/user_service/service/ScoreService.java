package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserScoreDto;
import school.faang.user_service.leaderboard.ScoreCalculator;
import school.faang.user_service.mapper.UserScoreMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScoreService {
    private static final int LEADERBOARD_MAX_SIZE = 15;
    private final UserScoreMapper userScoreMapper;
    private final UserRepository userRepository;
    private final List<ScoreCalculator> scoreCalculators;

    public Map<Long, Long> getUsersLeaderboard() {
        List<UserScoreDto> usersDto = userScoreMapper.toDto(userRepository.findAll());

        return usersDto.parallelStream()
                .collect(Collectors.toMap(
                        UserScoreDto::getUserId,
                        userDto -> scoreCalculators.stream()
                                .mapToLong(calculator -> calculator.getScore(userDto))
                                .sum()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(LEADERBOARD_MAX_SIZE)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
