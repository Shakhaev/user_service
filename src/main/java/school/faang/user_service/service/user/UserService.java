package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.event.SearchAppearanceEvent;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.model.Student;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SearchAppearanceEventPublisher searchAppearanceEventPublisher;
    private final CountryRepository countryRepository;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("There is no user with that id"));
    }

    public UserDto getUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no user with this ID"));
        log.info("Received a request to get the user with ID: {}", id);
        return userMapper.toDto(user);
    }

    public List<UserDto> getUsers(List<Long> ids) {
        log.info("Received a request to get the users with the following ids: {}", ids);
        return userRepository.findAllById(ids)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<Long> searchUsers(Long searchingUserId) {
        log.info("Received a request to get the users from the following user id: {}", searchingUserId);
        List<Long> userIds = List.of(1L, 2L, 3L);

        userIds.forEach(userId -> {
            SearchAppearanceEvent event = new SearchAppearanceEvent(userId, searchingUserId, LocalDateTime.now());
            searchAppearanceEventPublisher.publishSearchAppearanceEvent(event);
        });

        return userIds;
    }

    @Transactional
    public void registerUserFromCsv(InputStream inputStream) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.enable(CsvParser.Feature.TRIM_SPACES);

        MappingIterator<Student> iterator = csvMapper.readerFor(Student.class)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(inputStream);

        List<Student> students = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .collect(Collectors.toList());

        List<CompletableFuture<Void>> futures = students.stream()
                .map(student -> CompletableFuture.runAsync(() -> processStudent(student)))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        };

    private void processStudent(Student student) {
        User user = userMapper.toEntity(student);
        user.setPassword(userMapper.generateRandomPassword());

        Country country = Optional.ofNullable(countryRepository.findByTitle(student.getCountry()))
                .orElseGet(() -> {
                    Country newCountry = new Country();
                    newCountry.setTitle(student.getCountry());
                    return countryRepository.save(newCountry);
                });

        user.setCountry(country);
        userRepository.save(user);

        log.info("Received a request to save the user with ID: {}", user.getUsername());
    }
}