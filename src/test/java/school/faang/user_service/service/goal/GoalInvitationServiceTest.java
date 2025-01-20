package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.filter.invitation.InvitationFilter;
import school.faang.user_service.service.goal.filter.invitation.InvitationFilterIdInvited;
import school.faang.user_service.service.goal.filter.invitation.InvitationFilterIdInviter;
import school.faang.user_service.service.goal.filter.invitation.InvitationFilterNameInvited;
import school.faang.user_service.service.goal.filter.invitation.InvitationFilterNameInviter;
import school.faang.user_service.validator.goal.InvitationDtoValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {

    @InjectMocks
    private GoalInvitationServiceImpl goalInvitationService;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    @Spy
    private GoalInvitationMapperImpl goalInvitationMapper;

    @Mock
    private InvitationDtoValidator invitationDtoValidator;

    private GoalInvitationDto goalInvitationDtoReject;
    private GoalInvitation goalInvitationReject;

    private List<InvitationFilter> filters;

    @BeforeEach
    public void setUp() {
        goalInvitationReject = new GoalInvitation();
        goalInvitationReject.setId(1L);
        goalInvitationReject.setStatus(RequestStatus.PENDING);

        Goal goal = new Goal();
        goalInvitationReject.setGoal(goal);
        goalInvitationDtoReject =
                new GoalInvitationDto(null, null, null, null, RequestStatus.REJECTED);

        filters = new ArrayList<>();
        filters.add(new InvitationFilterIdInvited());
        filters.add(new InvitationFilterIdInviter());
        filters.add(new InvitationFilterNameInvited());
        filters.add(new InvitationFilterNameInviter());

        goalInvitationService = new GoalInvitationServiceImpl(goalInvitationRepository, invitationDtoValidator,
                goalInvitationMapper, null, filters);
    }


    @Test
    public void testCreateInvitation() {
        GoalInvitationDto goalInvitationDto = new GoalInvitationDto(null, null, null, null, null);
        GoalInvitation goalInvitation = new GoalInvitation();
        GoalInvitation savedInvitation = new GoalInvitation();
        GoalInvitationDto savedDto = new GoalInvitationDto(null, null, null, null, null);

        when(goalInvitationMapper.toEntity(goalInvitationDto)).thenReturn(goalInvitation);
        when(goalInvitationRepository.save(goalInvitation)).thenReturn(savedInvitation);
        when(goalInvitationMapper.toDto(savedInvitation)).thenReturn(savedDto);

        GoalInvitationDto result = goalInvitationService.createInvitation(goalInvitationDto);

        assertEquals(savedDto, result);

        verify(invitationDtoValidator, times(1)).validate(goalInvitationDto);
        verify(goalInvitationMapper, times(1)).toEntity(goalInvitationDto);
        verify(goalInvitationRepository, times(1)).save(goalInvitation);
        verify(goalInvitationMapper, times(1)).toDto(savedInvitation);
    }

    @Test
    void testCreateInvitationValidationFails() {
        GoalInvitationDto inputDto = new GoalInvitationDto(null, null, null, null, null);

        doThrow(new IllegalArgumentException("Validation failed"))
                .when(invitationDtoValidator).validate(inputDto);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> goalInvitationService.createInvitation(inputDto)
        );

        assertEquals("Validation failed", exception.getMessage());
        verify(invitationDtoValidator).validate(inputDto);
        verifyNoInteractions(goalInvitationRepository);
        verifyNoInteractions(goalInvitationMapper);
    }


    @Test
    public void testRejectGoalInvitationSuccess() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(goalInvitationReject));
        when(goalInvitationRepository.save(goalInvitationReject)).thenReturn(goalInvitationReject);
        when(goalInvitationMapper.toDto(goalInvitationReject)).thenReturn(goalInvitationDtoReject);

        GoalInvitationDto result = goalInvitationService.rejectGoalInvitation(1L);

        assertNotNull(result);
        assertEquals(RequestStatus.REJECTED, result.status());
    }

    @Test
    void testRejectGoalInvitationValidationFails() {
        GoalInvitationDto inputDto = new GoalInvitationDto(null, null, null, null, null);

        doThrow(new IllegalArgumentException("Validation failed"))
                .when(invitationDtoValidator).validate(inputDto);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> goalInvitationService.createInvitation(inputDto)
        );

        assertEquals("Validation failed", exception.getMessage());
        verify(invitationDtoValidator).validate(inputDto);
        verifyNoInteractions(goalInvitationRepository);
        verifyNoInteractions(goalInvitationMapper);
    }


    @Test
    void getInvitationsShouldMapGoalInvitationToDto() {
        GoalInvitation goalInvitation = new GoalInvitation();
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        goalInvitation.setInvited(user1);
        goalInvitation.setInviter(user2);

        when(goalInvitationRepository.findAll()).thenReturn(Collections.singletonList(goalInvitation));

        InvitationFilterDto invitationFilterDto = new InvitationFilterDto(null,null,
                null,1L,null);

        List<GoalInvitationDto> result = goalInvitationService.getInvitations(invitationFilterDto);

        assertNotNull(result, "Expected a non-null result.");
        assertEquals(1, result.size(), "Expected exactly one goal invitation DTO.");
    }
}