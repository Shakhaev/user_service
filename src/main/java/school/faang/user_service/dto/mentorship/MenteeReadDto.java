package school.faang.user_service.dto.mentorship;

import lombok.Data;

import java.util.List;

@Data
public class MenteeReadDto {
    private Long id;
    private List<Long> menteesId;
}
