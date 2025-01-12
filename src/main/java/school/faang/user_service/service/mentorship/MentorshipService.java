package school.faang.user_service.service.mentorship;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.mapper.UserMapper;

import java.util.List;

@Service
@AllArgsConstructor
public class MentorshipService {

    private final UserMapper userMapper;

    private final MentorshipRepository mentorshipRepository;

    public List<UserDto> getMentees(long mentorId) {
        return userMapper.toDto(mentorshipRepository.findMenteesByUserId(mentorId));
    }
}