package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.dto.transfer.Exist;
import school.faang.user_service.dto.transfer.New;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    @NotNull(groups = {Exist.class})
    @Null(groups = {New.class})
    private Long id;

    @NotNull(groups = {New.class})
    private String username;

    @NotNull(groups = {New.class})
    @Email(groups = {New.class})
    private String email;
}