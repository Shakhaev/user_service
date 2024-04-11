package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.s3.MinioConfig;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3_minio_service.S3Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Value("${dicebear.pic-base-url}")
    private String large_avatar;

    @Value("${dicebear.pic-base-url-small}")
    private String small_avatar;

    private final S3Service s3Service;
    private final MinioConfig minioConfig;

    public UserDto getUser(long userId) {
        User user = userRepository.findById( userId ).orElseThrow( () -> new NoSuchElementException( "User not found!" ) );
        return userMapper.toDto( user );
    }

    public UserDto create(UserDto userDto) {

        checkUserAlreadyExists( userDto );

        User user = userMapper.toEntity( userDto );
        user.setUserProfilePic( getRandomAvatar() );
        user.setActive( true );

        User createdUser = userRepository.save( user );
        String fileNameSmallAva = "small_" + user.getId() + ".svg";
        String fileNameLargeAva = "large_" + user.getId() + ".svg";

        s3Service.
                saveSvgToS3( user.getUserProfilePic().getSmallFileId(),
                        minioConfig.getBucketName(),
                        fileNameSmallAva );
        s3Service.
                saveSvgToS3( user.getUserProfilePic().getFileId(),
                        minioConfig.getBucketName(),
                        fileNameLargeAva );
        return userMapper.toDto( createdUser );

    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userMapper.toDto( userRepository.findAllById( ids ) );
    }

    private UserProfilePic getRandomAvatar() {

        UUID seed = UUID.randomUUID();
        return UserProfilePic.builder().
                smallFileId( small_avatar + seed ).
                fileId( large_avatar + seed ).build();

    }

    private void checkUserAlreadyExists(UserDto userDto) {

        boolean exists = userRepository.findById( userDto.getId() ).isPresent();

        if (exists) {
            log.error( "User with id " + userDto.getId() + " exists" );
            throw new DataValidationException( "User with id " + userDto.getId() + " exists" );
        }
    }
}
