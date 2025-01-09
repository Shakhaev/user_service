package school.faang.user_service.dto;

import school.faang.user_service.entity.RequestStatus;

public record GoalInvitationDto(Long id, Long inviterId, Long invitedUserId,
                                Long goalId, RequestStatus status) { }
