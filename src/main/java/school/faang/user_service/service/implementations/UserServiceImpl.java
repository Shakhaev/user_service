package school.faang.user_service.service.implementations;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import school.faang.user_service.dto.country.CountryDto;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.CountryMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import school.faang.user_service.service.CountryService;
import school.faang.user_service.service.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CountryService countryService;
    private final UserMapper userMapper;
    private final CountryMapper countryMapper;
    private final WebClient webClient;

    @Override
    public UserDto getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new DataValidationException("User by ID is not found"));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        CountryDto countryDto = countryService.getCountryByName(userCreateDto.country());
        User newUser = userMapper.toEntity(userCreateDto);
        newUser.setActive(true);
        newUser.setCountry(countryMapper.toEntity(countryDto));
        User savedUser = userRepository.save(newUser);

        return userMapper.toDto(savedUser);
    }

    public void addAvatarToUser() {
        ResponseEntity<ByteArrayResource> responseEntity = webClient
                .get()
                .uri("/open-peeps/svg")
                .retrieve()
                .toEntity(ByteArrayResource.class)
                .doOnError(ex -> log.error("Ошибка при запросе к сервису DiceBear: ", ex))
                .block();

        if(responseEntity == null) {
            throw new RuntimeException("Ошибка при запросе к сервису DiceBear");
        }

        byte[] byteArray = Objects.requireNonNull(responseEntity.getBody(), "Пустой ответ от сервиса DiceBear")
                .getByteArray();

        InputStream in = new ByteArrayInputStream(byteArray);
        try{
            BufferedImage bufferedImage = ImageIO.read(in);
            File svg = new File("image.svg");
            ImageIO.write(bufferedImage, "SVG", svg);
        } catch (IOException ex) {
            log.error("Ошибка при обработке файла ", ex);
            throw new RuntimeException("Какой-то там IOException)))");
        }

        // идём сервис герерации картинок
        // берём оттуда картинку аватара в двух размерах
        // сохраняем их в Амазон С3
        // берёмм ссылки картинок
        // добавляем их к сущности юзера
        // сохраняем в базе
    }
}