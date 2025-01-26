package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<UserDto> getMentors(Long menteeId) {
        if (menteeId == null) {
            throw new DataValidationException("mentee id can't be null");
        }
        return mentorshipService.getMentors(menteeId);
    }
}
