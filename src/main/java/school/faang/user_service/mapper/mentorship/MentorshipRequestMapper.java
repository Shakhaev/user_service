package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipRequestMapper {

    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    MentorshipRequest toEntity(MentorshipRequestDto mentorshipRequestDto);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    MentorshipRequestDto toDto(MentorshipRequest mentorshipRequest);

    default List<MentorshipRequestDto> toEntityList(List<MentorshipRequest> requestsList) {
        return requestsList.stream()
                .map(this::toDto)
                .toList();
    }

}
