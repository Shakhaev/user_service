package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MentorshipRequestMapper {

    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "requesterId", source = "requester.id")
    MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest mentorshipRequest);

    MentorshipRequest toMentorshipRequest(MentorshipRequestDto mentorshipRequestDto);

    List<MentorshipRequestDto> toMentorshipRequestDto(List<MentorshipRequest> mentorshipRequests);
}
