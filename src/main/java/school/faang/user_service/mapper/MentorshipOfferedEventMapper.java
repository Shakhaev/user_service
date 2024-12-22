package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorshipOfferedEvent;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public class MentorshipOfferedEventMapper {

    public MentorshipOfferedEvent toMentorshipOfferedEvent(MentorshipRequest mentorshipRequest) {
        if (mentorshipRequest == null) {
            throw new IllegalArgumentException("MentorshipRequest cannot be null");
        }

        return new MentorshipOfferedEvent(
                mentorshipRequest.getId(),
                mentorshipRequest.getReceiver().getId(),
                mentorshipRequest.getRequester().getId()
        );
    }
}
