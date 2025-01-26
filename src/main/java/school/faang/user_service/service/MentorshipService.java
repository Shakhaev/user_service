package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;

    public List<UserDto> getMentors(Long menteeId) {
        User mentee = mentorshipRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException("mentee with id = " + menteeId + " not found"));
        return mentee.getMentors().stream()
                .map(userMapper::toUserDto)
                .toList();
    }
}
