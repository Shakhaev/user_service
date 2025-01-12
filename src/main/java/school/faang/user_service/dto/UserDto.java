package school.faang.user_service.dto;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.dto.transfer.Details;
import school.faang.user_service.dto.transfer.Exist;
import school.faang.user_service.dto.transfer.New;

@AllArgsConstructor
@Getter
@Setter
public class UserDto {
    @NotNull(groups = {Exist.class})
    @Null(groups = {New.class})
    @JsonView({Details.class})
    private Long id;

    @NotNull(groups = {New.class})
    @JsonView({Details.class})
    private String username;

    @NotNull(groups = {New.class})
    @Email(groups = {New.class})
    @JsonView({Details.class})
    private String email;
}