package school.faang.user_service.dto.mentorship;

import lombok.Data;

import java.util.List;

@Data
public class MentorReadDto {
    private List<Long> mentorsId;
}
