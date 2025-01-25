package school.faang.user_service.dto.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFilterDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private boolean isPremium;
}
