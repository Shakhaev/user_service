package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Test
    public void testNullTitleIsValid() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> goalService.save(null, "description", null));
    }

    @Test
    public void testEmptyTitleIsValid() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> goalService.save("   ", "description", null));
    }

    @Test
    public void testGoalIsSaved() {
        Mockito.when(goalRepository.create(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyLong()
        )).thenReturn(new Goal());

        goalService.save("title", "description", 1L);
        Mockito.verify(goalRepository, Mockito.times(1))
                .create(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
    }

}
