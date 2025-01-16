package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.rating.LeaderTableDto;
import school.faang.user_service.dto.rating.RatingDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.rating.description.Descriptionable;
import school.faang.user_service.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final ApplicationEventPublisher publisher;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(RatingService.class);

    @Transactional
    public List<LeaderTableDto> getTableLeaders(int limit) {
        List<User> users = userRepository.findAll();

        return users.stream()
                .sorted(Comparator.comparingInt(User::getRatingPoints).reversed())
                .map(user -> new LeaderTableDto(user.getId(),user.getUsername(), user.getEmail(), user.getRatingPoints()))
                .limit(limit)
                .toList();
    }

    public void addPoints(RatingDto ratingDTO) {
        logger.info("Publishing event -> {}, {}, {}, {}", ratingDTO.id(), ratingDTO.actionType(), ratingDTO.descriptionable(), ratingDTO.points());

        publisher.publishEvent(ratingDTO);
    }

    public void addRating(Descriptionable descriptionable, long userId,
                           int points, ActionType actionType) {
        RatingDto ratingDTO = new RatingDto(
                descriptionable,
                userId,
                points,
                actionType
        );

        addPoints(ratingDTO);
    }
}
