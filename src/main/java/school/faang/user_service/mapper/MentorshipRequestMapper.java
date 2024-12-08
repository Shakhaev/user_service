package school.faang.user_service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;

@Mapper(componentModel = "spring")
public interface MentorshipRequestMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "requesterId", target = "requester.id")
    @Mapping(source = "receiverId", target = "receiver.id")
    MentorshipRequest toEntity(MentorshipRequestDto dto);

    @Mapping(source = "requester.id", target = "requesterId")
    @Mapping(source = "receiver.id", target = "receiverId")
    MentorshipRequestDto toDto(MentorshipRequest entity);
}
