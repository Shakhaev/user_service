package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.kafka.KafkaTopicsProps;
import school.faang.user_service.dto.user.UserProfileCreateDto;
import school.faang.user_service.dto.user.UserProfileResponseDto;
import school.faang.user_service.dto.user.UserProfileUpdateDto;
import school.faang.user_service.dto.user.UserSearchResponse;
import school.faang.user_service.message.producer.KeyedMessagePublisher;
import school.faang.user_service.model.jpa.Country;
import school.faang.user_service.model.jpa.User;
import school.faang.user_service.repository.jpa.UserRepository;
import school.faang.user_service.service.ReindexingService;
import school.faang.user_service.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserV1Controller {

    private final UserService userService;
    private final KeyedMessagePublisher keyedMessagePublisher;
    private final KafkaTopicsProps kafkaTopicsProps;
    private final ReindexingService reindexingService;
    private final UserRepository userRepository;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String DEFAULT_SORT_FIELD = "id";

    @PostMapping("/profile/{userId}")
    public UserProfileResponseDto createUserProfile(@RequestBody UserProfileCreateDto userProfileCreateDto) {
        return userService.createUserProfile(userProfileCreateDto);
    }

    @PutMapping("/profile/{userId}")
    public UserProfileResponseDto updateUserProfile(@Positive @PathVariable long userId,
                                                    @RequestBody UserProfileUpdateDto userProfileUpdateDto) {
        return userService.updateUserProfile(userId, userProfileUpdateDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserSearchResponse getUserById(@PathVariable @Positive long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/jobs/reindex")
    public void reindex() throws JobExecutionException {
        reindexingService.reindexAllUsers();
    }

    @GetMapping("/test/search")
    public Page<User> getUsersByExperience(
            @PageableDefault(
                    size = DEFAULT_PAGE_SIZE,
                    sort = DEFAULT_SORT_FIELD,
                    page = 0,
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        Page<User> users = userRepository.findAllByExperienceBetween(1, 100, pageable);
        return users;
    }

    @PostMapping("/test/generate")
    public void generateUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 10000; i++) {
            User user = User.builder()
                    .username("user_" + i)
                    .email("user_" + i + "@example.com")
                    .phone("+123456789" + i)
                    .password(UUID.randomUUID().toString()) // Генерируем случайный пароль
                    .active(true)
                    .aboutMe("This is a generated user #" + i)
                    .country(Country.builder().id(1).title("United States").build())
                    .city("City_" + i % 100)
                    .experience(i % 50) // Чтобы заполнить опытом случайные значения
                    .build();
            users.add(user);
        }
        userRepository.saveAll(users);
    }

    @DeleteMapping("/test/{id}")
    public void deleteUser(@PathVariable @Positive Long id) {
        keyedMessagePublisher.send(kafkaTopicsProps.getUserIndexingTopic().getName(), id.toString(), null);
    }
}
