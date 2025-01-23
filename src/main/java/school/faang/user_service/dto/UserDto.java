package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDto {
    private Long userId;
    private String username;
    private String email;
    private String password;
    private boolean active;
    private List<MentorshipRequestDto> sentMentorshipRequests;
}
