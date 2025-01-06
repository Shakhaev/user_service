package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.exceptions.DataValidationException;

@Component
public class MentorshipRequestValidator {

    public void validateReceiverAndActorIdsAreNotTheSame(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.receiverId().equals(mentorshipRequestDto.requesterId())) {
            throw new DataValidationException("Can not send mentorship request to yourself");
        }
    }
}
