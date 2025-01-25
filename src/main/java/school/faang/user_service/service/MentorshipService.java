package school.faang.user_service.service;

import school.faang.user_service.entity.User;

import java.util.List;

public interface MentorshipService {
    List<User> getMentors(Long userId);
    List<User> getMentees(Long userId);
    void deleteMentee(Long menteeId, Long mentorId);
    void deleteMentor(Long menteeId, Long mentorId);
}
