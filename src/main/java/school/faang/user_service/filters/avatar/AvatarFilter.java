package school.faang.user_service.filters.avatar;

import school.faang.user_service.entity.user.UserProfilePic;

import java.awt.image.BufferedImage;

public interface AvatarFilter {
    void resizeAndUploadToMinio(BufferedImage originalImage,
                                String formatName,
                                UserProfilePic userProfilePic);
}
