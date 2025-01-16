package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface MentorshipRequestMapper {

    @Mapping(target = "requesterId", expression = "java(userToId(mentorshipRequest.getRequester()))")
    @Mapping(target = "receiverId", expression = "java(userToId(mentorshipRequest.getReceiver()))")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);


    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    @Mapping(target = "requesterId", expression = "java(userToId(mentorshipRequest.getRequester()))")
    @Mapping(target = "receiverId", expression = "java(userToId(mentorshipRequest.getReceiver()))")
    List<MentorshipRequestDto> toDto(List<MentorshipRequest> mentorshipRequests);

    default Long userToId(User user) {
        return user.getId();
    }
}
