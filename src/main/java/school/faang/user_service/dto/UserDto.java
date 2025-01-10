package school.faang.user_service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class UserDto {
    private int id;
    private List<Long> menteesIds;

}
