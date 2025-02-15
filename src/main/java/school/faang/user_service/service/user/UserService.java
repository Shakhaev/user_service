package school.faang.user_service.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.Person;
import school.faang.user_service.dto.user.UpdateUsersRankDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.exception.data.DataValidationException;
import school.faang.user_service.mapper.csv.CsvParser;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.service.country.CountryService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final static int BATCH_SIZE = 50;

    @PersistenceContext
    private final EntityManager entityManager;
    private final UserMapper userMapper;
    private final UserContext userContext;
    private final AvatarService avatarService;
    private final CsvParser csvParser;
    private final CountryService countryService;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;

    public Optional<User> findById(long userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public ResponseEntity<Void> updateUsersRankByUserIds(UpdateUsersRankDto userDto) {
        log.info("batch is starting {}", userDto);
        int batchCounter = 1;
        for (Map.Entry<Long, Double> userNewRank : userDto.getUsersRankByIds().entrySet()) {
            if (userNewRank.getValue() != 0.0) {
                BigDecimal value = BigDecimal.valueOf(userNewRank.getValue());
                double roundedValue = value.setScale(2, RoundingMode.HALF_UP).doubleValue();
                try {
                    userRepository.updateUserRankByUserId(userNewRank.getKey(), roundedValue);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (roundedValue > userDto.getHalfUserRank()) {
                        userRepository.updateUserRankByUserIdToMax(userNewRank.getKey(), BigDecimal.valueOf(userDto.getMaximumUserRating()));
                    } else {
                        userRepository.updateUserRankByUserIdToMin(userNewRank.getKey(), BigDecimal.valueOf(userDto.getMinimumUserRating()));
                    }
                }
                batchCounter++;
            }
            if (batchCounter % BATCH_SIZE == 0) {
                flushAndClear();
            }
        }
        updatePassiveUsersRating(userDto);
        log.info("users rank success updated!");
        return ResponseEntity.ok().build();
    }

    @Transactional
    public void updatePassiveUsersRating(UpdateUsersRankDto userDto) {
        BigDecimal maxPossibleRating = BigDecimal.valueOf(userDto.getMaximumGrowthRating() * userDto.getRatingGrowthIntensive());
        BigDecimal roundedValue = maxPossibleRating.setScale(2, RoundingMode.HALF_UP);
        Set<Long> activeUsersIds = userDto.getUsersRankByIds().keySet();
        userRepository.updatePassiveUsersRatingWhichRatingLessThanRating(roundedValue.doubleValue(), activeUsersIds);
        userRepository.updatePassiveUsersRatingWhichRatingMoreThanRating(roundedValue.doubleValue(), activeUsersIds);
        flushAndClear();
    }

    @Transactional
    public void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    public String generateRandomAvatar() {
        User user = getUserOrThrowException(userContext.getUserId());
        String randomAvatarUrl = avatarService.generateRandomAvatar(UUID.randomUUID().toString(),
                user.getId() + ".svg");
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(randomAvatarUrl);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
        return randomAvatarUrl;
    }

    @Transactional
    public String saveCustomAvatar(MultipartFile file) {
        log.info("Start loading custom avatar for user with id={}.", userContext.getUserId());
        User user = getUserOrThrowException(userContext.getUserId());
        avatarService.validateCustomAvatarSize(file);

        UserProfilePic userProfilePic = avatarService.uploadCustomAvatar(file);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        log.info("Successful completion of uploading a custom avatar for user id={}", userContext.getUserId());
        return getAvatar(false);
    }

    @Transactional
    public void deleteAvatar() {
        log.info("Start deleting user avatar with id={}", userContext.getUserId());
        User user = getUserOrThrowException(userContext.getUserId());
        avatarService.checkUserHasAvatar(user);

        avatarService.deleteAvatar(user.getUserProfilePic());
        user.setUserProfilePic(null);
        userRepository.save(user);
        log.info("Successful deletion of user avatar with id={}", userContext.getUserId());
    }

    public String getAvatar(boolean isSmall) {
        log.info("Start receiving user avatar with id={}; isSmall={}", userContext.getUserId(), isSmall);
        User user = getUserOrThrowException(userContext.getUserId());
        avatarService.checkUserHasAvatar(user);

        log.info("Successfully retrieve avatar of user id={}; isSmall={}", userContext.getUserId(), isSmall);
        return avatarService.getAvatar(user.getUserProfilePic(), isSmall);
    }

    private User getUserOrThrowException(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User doesn't exist."));
    }

    public UserDto getUserDtoById(long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("user not found!"));
        return userMapper.toDto(user);
    }

    public List<UserDto> getUserDtosByIds(List<Long> userIds) {
        List<User> users = userRepository.findAllByIds(userIds)
                .orElseThrow(() -> new DataValidationException("users not found!"));
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    public UserSkillGuarantee addGuaranty(long userId, SkillOffer skillOffer) {
        UserSkillGuarantee guarantee = UserSkillGuarantee.builder().user(
                        userRepository.findById(userId).get()
                ).guarantor(skillOffer.getRecommendation().getAuthor())
                .build();
        return userSkillGuaranteeRepository.save(guarantee);
    }

    public void uploadUsers(MultipartFile file) {
        log.info("upload csv file: {} starting", file.getOriginalFilename());
        List<Person> parsedPersons = csvParser.parseCsv(file, Person.class);
        List<CompletableFuture<User>> futureUsers = parsedPersons.stream()
                .map(person -> CompletableFuture.supplyAsync(() -> {
                    User user = userMapper.toEntity(person);
                    user.setPassword(generateRandomPassword(user));
                    user.setCountry(countryService.getCountryOrCreateByName(user));
                    return user;
                }, Executors.newCachedThreadPool()))
                .toList();

        List<User> users = futureUsers.stream()
                .map(CompletableFuture::join)
                .toList();
        try {
            userRepository.saveAll(users);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(e.getMostSpecificCause().getLocalizedMessage());
        }
        log.info("success saved all users form file");
    }

    public String generateRandomPassword(User user) {
        return user.getEmail();
    }

    public void deactivateUser(long userId) {
        Optional<User> userOptional = findById(userId);
        if (userOptional.isEmpty()) {
            throw new DataValidationException("User not found by current id");
        }
        User user = userOptional.get();
        if (!user.isActive()) {
            throw new DataValidationException("User is already deactivated");
        }

        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
