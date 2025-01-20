package school.faang.user_service.filters.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationFilterTest {
    Stream<GoalInvitation> stream;
    private GoalInvitationFilter filter;
    private GoalInvitation goalInvitationFirst;
    private GoalInvitation goalInvitationSecond;
    private GoalInvitationFilterDto filterDto;

    @BeforeEach
    public void init() {
        goalInvitationFirst = new GoalInvitation();
        goalInvitationFirst.setInvited(User.builder()
                .username("Josh")
                .id(1L)
                .build());
        goalInvitationFirst.setInviter(User.builder()
                .username("John")
                .id(2L)
                .build());
        goalInvitationFirst.setStatus(RequestStatus.ACCEPTED);

        goalInvitationSecond = new GoalInvitation();
        goalInvitationSecond.setInvited(User.builder()
                .username("John")
                .id(2L)
                .build());
        goalInvitationSecond.setInviter(User.builder()
                .username("Josh")
                .id(1L)
                .build());
        goalInvitationSecond.setStatus(RequestStatus.PENDING);

        stream = Stream.of(goalInvitationFirst, goalInvitationSecond);
        filterDto = new GoalInvitationFilterDto();
    }

    @Test
    public void applyInvitedIdSuccess() {
        filter = new GoalInvitationInvitedIdFilter();
        filterDto.setInvitedId(goalInvitationSecond.getInvited().getId());

        List<GoalInvitation> result = filter.apply(stream, filterDto).collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals(goalInvitationSecond, result.get(0));
    }

    @Test
    public void applyInviterIdSuccess() {
        filter = new GoalInvitationInviterIdFilter();
        filterDto.setInviterId(goalInvitationSecond.getInviter().getId());

        List<GoalInvitation> result = filter.apply(stream, filterDto).collect(Collectors.toList());

        assertEquals(1, result.size());
        assertEquals(goalInvitationSecond, result.get(0));
    }

    @Test
    public void applyInvitedNameSuccess(){
        filter = new GoalInvitationInvitedNameFilter();
        filterDto.setInvitedNamePattern(goalInvitationSecond.getInvited().getUsername());

        List<GoalInvitation> result = filter.apply(stream, filterDto).collect(Collectors.toList());
        assertEquals(1, result.size());
        assertEquals(goalInvitationSecond.getInvited().getUsername(), result.get(0).getInvited().getUsername());
    }

    @Test
    public void applyInviterNameSuccess(){
        filter = new GoalInvitationInviterNameFilter();
        filterDto.setInviterNamePattern(goalInvitationSecond.getInviter().getUsername());

        List<GoalInvitation> result = filter.apply(stream, filterDto).collect(Collectors.toList());
        assertEquals(1, result.size());
        assertEquals(goalInvitationSecond.getInviter().getUsername(), result.get(0).getInviter().getUsername());
    }

    @Test
    public void applyNotFullNameTest(){
        goalInvitationFirst.getInviter().setUsername("Steve");
        goalInvitationFirst.getInvited().setUsername("Harry");
        filter = new GoalInvitationInvitedNameFilter();
        filterDto.setInvitedNamePattern("Jo");
        filterDto.setInviterNamePattern("Jo");

        List<GoalInvitation> result = filter.apply(stream, filterDto).collect(Collectors.toList());
        assertEquals(1, result.size());
        assertEquals(goalInvitationSecond, result.get(0));
    }

    @Test
    public void applyStatusFilterSuccess(){
        filter = new GoalInvitationStatusFilter();
        filterDto.setStatus(goalInvitationSecond.getStatus());

        List<GoalInvitation> result = filter.apply(stream, filterDto).collect(Collectors.toList());
        assertEquals(1, result.size());
        assertEquals(goalInvitationSecond, result.get(0));
    }
}
