package school.faang.user_service.controller.mentorship;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestControllerTest {

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @Spy
    private MentorshipRequestMapper requestMapper;

    @Test
    public void testDescriptionIsNull() {
        MentorshipRequestDto testDto = MentorshipRequestDto.builder()
                .description(null)
                .build();

        Assert.assertThrows(
                DataValidationException.class,
                () -> mentorshipRequestController.requestMentorship(testDto));
    }

    @Test
    public void testDescriptionIsEmpty() {
        MentorshipRequestDto testDto = MentorshipRequestDto.builder()
                .description("")
                .build();

        Assert.assertThrows(
                DataValidationException.class,
                () -> mentorshipRequestController.requestMentorship(testDto));
    }

    @Test
    public void testValidRequestDto() {
        MentorshipRequestDto testDto = MentorshipRequestDto.builder()
                .description("reason")
                .requesterId(1L)
                .receiverId(2L)
                .build();

        MentorshipRequest requestEntity = requestMapper.toEntity(testDto);

        mentorshipRequestController.requestMentorship(testDto);
        verify(mentorshipRequestService, Mockito.times(1)).requestMentorship(requestEntity);
    }

    @Test
    public void testGetRequests() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setDescription("test");

        mentorshipRequestController.getRequests(filterDto);
        verify(mentorshipRequestService, Mockito.times(1)).getRequests(filterDto);
    }

    @Test
    public void testAcceptRequest() {
        mentorshipRequestController.acceptRequest(7L);
        verify(mentorshipRequestService, Mockito.times(1)).acceptRequest(7L);
    }

    @Test
    public void testRejectRequest() {
        RejectionDto reject = RejectionDto.builder()
                .id(3L)
                .rejectionReason("duplicate")
                .build();

        mentorshipRequestController.rejectRequest(reject.getId(), reject);
        verify(mentorshipRequestService, Mockito.times(1)).rejectRequest(reject.getId(), reject);
    }
}
