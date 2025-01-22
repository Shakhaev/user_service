package school.faang.user_service.service.user;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {

    ByteArrayResource generateAvatar(String seed, int size);

    void uploadAvatar(ByteArrayResource file, String uuid);

    void uploadAvatar(MultipartFile file);
}
