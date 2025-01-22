package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCacheDto {

    private long id;
    private String username;
    private UserProfilePicDto profilePicture;
}
