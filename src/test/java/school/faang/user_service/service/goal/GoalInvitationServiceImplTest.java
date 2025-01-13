package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.goal.filter.invitation.InvitationFilter;
import school.faang.user_service.validator.goal.InvitationDtoValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceImplTest {

    @InjectMocks
    private GoalInvitationServiceImpl goalInvitationServiceImpl;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalInvitationMapper goalInvitationMapper;
    @Mock
    private List<InvitationFilter> invitationFilters;
    @Mock
    private InvitationDtoValidator invitationDtoValidator;

    private GoalInvitationDto goalInvitationDtoReject;
    private GoalInvitation goalInvitationReject;

    @BeforeEach
    public void setUp() {
        goalInvitationReject = new GoalInvitation();
        goalInvitationReject.setId(1L);
        goalInvitationReject.setStatus(RequestStatus.PENDING);

        Goal goal = new Goal();
        goalInvitationReject.setGoal(goal);
        goalInvitationDtoReject =
                new GoalInvitationDto(null, null, null, null, RequestStatus.REJECTED);
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

        GoalInvitationDto result = goalInvitationServiceImpl.createInvitation(goalInvitationDto);

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
                () -> goalInvitationServiceImpl.createInvitation(inputDto)
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

        GoalInvitationDto result = goalInvitationServiceImpl.rejectGoalInvitation(1L);

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
                () -> goalInvitationServiceImpl.createInvitation(inputDto)
        );

        assertEquals("Validation failed", exception.getMessage());
        verify(invitationDtoValidator).validate(inputDto);
        verifyNoInteractions(goalInvitationRepository);
        verifyNoInteractions(goalInvitationMapper);
    }

    @Test
    public void testGetInvitationsByFilter() {
        InvitationFilterDto filters = new InvitationFilterDto(null, null, null, null, null);
        GoalInvitation goalInvitation1 = new GoalInvitation();
        GoalInvitation goalInvitation2 = new GoalInvitation();
        List<GoalInvitation> goalInvitations = List.of(goalInvitation1, goalInvitation2);

        when(goalInvitationRepository.findAll()).thenReturn(goalInvitations);

        InvitationFilter filter1 = mock(InvitationFilter.class);
        InvitationFilter filter2 = mock(InvitationFilter.class);
        when(invitationFilters.stream()).thenReturn(Stream.of(filter1, filter2));

        when(filter1.isAcceptable(filters)).thenReturn(true);
        when(filter2.isAcceptable(filters)).thenReturn(false);

        when(filter1.apply(any(), eq(filters))).thenReturn(Stream.of(goalInvitation1));

        GoalInvitationDto dto1 = new GoalInvitationDto(null, null, null, null, null);
        when(goalInvitationMapper.toDto(goalInvitation1)).thenReturn(dto1);

        List<GoalInvitationDto> result = goalInvitationServiceImpl.getInvitations(filters);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));

        verify(goalInvitationRepository, times(1)).findAll();
        verify(filter1, times(1)).isAcceptable(filters);
        verify(filter2, times(1)).isAcceptable(filters);
        verify(filter1, times(1)).apply(any(), eq(filters));
        verify(filter2, times(0)).apply(any(), eq(filters));
    }
}




