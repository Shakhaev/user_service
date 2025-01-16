package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.rating.LeaderTableDto;
import school.faang.user_service.dto.rating.RatingDto;
import school.faang.user_service.dto.rating.UserComparingDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.LeaderTableMapper;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.rating.description.Descriptionable;
import school.faang.user_service.rating.factory.ComparatorFactory;
import school.faang.user_service.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final ApplicationEventPublisher publisher;
    private final UserRepository userRepository;
    private final List<Comparator<User>> comparators;
    private final LeaderTableMapper leaderTableMapper;
    private final ComparatorFactory comparatorFactory;
    private static final Logger logger = LoggerFactory.getLogger(RatingService.class);

    @Transactional
    public List<LeaderTableDto> getTableLeaders(int limit, UserComparingDto userComparingDto) {
        List<User> users = userRepository.findAll();
        Stream<User> userStream = users.stream();

        Comparator<User> comparator = comparatorFactory.getComparator(userComparingDto);
        if (comparator != null) {
            userStream = userStream.sorted(comparator);
        }

        return userStream.limit(limit)
                .map(leaderTableMapper::toDto)
                .toList();
    }

    private Stream<LeaderTableDto> filterPeople(Stream<User> followersOfUser, UserComparingDto userComparingDto) {
        Stream<User> userStream = followersOfUser.parallel();
        for (Comparator<User> comparator : comparators) {
            userStream = userStream.sorted(comparator);
        }
        return userStream.map(leaderTableMapper::toDto);
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
