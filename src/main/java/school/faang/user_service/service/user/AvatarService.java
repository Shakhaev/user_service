package school.faang.user_service.service.user;

import org.springframework.core.io.ByteArrayResource;

public interface AvatarService {

    ByteArrayResource getAvatarFromDiceBear(String seed, int size);

    String uploadAvatar(ByteArrayResource file);

}
