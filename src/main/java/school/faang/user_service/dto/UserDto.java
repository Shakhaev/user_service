package school.faang.user_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private boolean active;
    private String aboutMe;
    private Long countryId;
    private List<Long> skillsIds;
    private List<Long> ownedEventsIds;
    private List<Long> participatedEventsIds;
}
