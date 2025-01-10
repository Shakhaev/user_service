/*
package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.controller.goal.GoalController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.filters.goal.GoalFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    //   private static Long userId;
    //   private final List<GoalFilter> goalFilters = new ArrayList<>();

*/
/*
    @Mock
    private GoalRepository goalRepository;

    @Mock
    private List<GoalFilter> goalFilters;
    //  private GoalMapper goalMapper;

*//*

*/
/*    @Spy
    private GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);*//*
*/
/*


    @InjectMocks
    private GoalService goalService;


    @BeforeEach
    public void initialize() {
        MockitoAnnotations.openMocks(this);
   *//*

*/
/*     userId = 1000L;
        goalFilters.add(new TitleFilter());
        goalFilters.add(new StatusFilter());
        goalService = new GoalService(goalRepository, goalMapper, goalFilters);*//*
*/
/*

    }
*//*


    */
/*@Test
    public void testGetGoalsByUserId() {
        long userId = 1L;
        GoalFilterDto filterDto = new GoalFilterDto();
        Stream<Goal> goalsStream = Stream.of(new Goal(), new Goal());
        List<Goal> expectedGoals = List.of(new Goal(), new Goal());

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goalsStream);
        when(goalService.filterGoals(goalsStream, filterDto)).thenReturn(expectedGoals);

        List<Goal> result = goalService.getGoalsByUserId(userId, filterDto);

        assertEquals(expectedGoals, result);
        verify(goalRepository, times(1)).findGoalsByUserId(userId);
        verify(goalService, times(1)).filterGoals(goalsStream, filterDto);
    }

    @Test
    public void testFilterGoals() {
        Stream<Goal> goalsStream = Stream.of(new Goal(), new Goal());
        GoalFilterDto filterDto = new GoalFilterDto();
        List<Goal> expectedGoals = List.of(new Goal(), new Goal());

        GoalFilter filter1 = mock(GoalFilter.class);
        GoalFilter filter2 = mock(GoalFilter.class);

        when(filter1.isApplicable(filterDto)).thenReturn(true);
        when(filter2.isApplicable(filterDto)).thenReturn(false);
        when(filter1.apply(any(Stream.class), eq(filterDto))).thenReturn(goalsStream);

        goalFilters.add(filter1);
        goalFilters.add(filter2);

        List<Goal> result = goalService.filterGoals(goalsStream, filterDto);

        assertEquals(expectedGoals, result);
        verify(filter1, times(1)).isApplicable(filterDto);
        verify(filter2, times(1)).isApplicable(filterDto);
        verify(filter1, times(1)).apply(any(Stream.class), eq(filterDto));
        verify(filter2, never()).apply(any(Stream.class), eq(filterDto));
    }*//*


*/
/*    @Test
    public void testGetGoalsByUserId() {
        Goal goal1 = new Goal();
        goal1.setId(1L);
        goal1.setTitle("Goal1");
        goal1.setStatus(GoalStatus.ACTIVE);

        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Goal2");
        goal2.setStatus(GoalStatus.ACTIVE);

     //   List<Long> skillIds = new ArrayList<>();
       *//*
*/
/* GoalDto goalDto1 = new GoalDto(1L, "Description", null, "Goal1", GoalStatus.ACTIVE, skillIds);
        GoalDto goalDto2 = new GoalDto(2L, "Description", null, "Goal2", GoalStatus.ACTIVE, skillIds);*//*
*/
/*

        GoalDto goalDto1 = new GoalDto(1L, "Description", null, "Goal1", GoalStatus.ACTIVE, null);
        GoalDto goalDto2 = new GoalDto(2L, "Description", null, "Goal2", GoalStatus.ACTIVE, null);


        GoalFilterDto goalFilterDto = new GoalFilterDto("Goal1", GoalStatus.ACTIVE);

       // Мокируем поведение репозитория и маппера
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal1, goal2));
     //   when(goalMapper.toDto(goal1)).thenReturn(goalDto1);
     //   when(goalMapper.toDto(goal2)).thenReturn(goalDto2);

        when(goalMapper.toDto(any(Goal.class))).thenReturn(GoalDto);

        // Вызываем метод сервиса
        List<GoalDto> result = goalService.getGoalsByUserId(userId, goalFilterDto);

        // Проверяем результат
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Goal1", result.get(0).getTitle());
    //    assertNotEquals("Goal1", result.get(0).getTitle());
        //   assertEquals("Goal2", result.get(1).getTitle());
    }*//*


    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private List<GoalFilter> goalFilters;

    @Mock
    private GoalFilter titleGoalFilter;
    @Mock
    private GoalFilter statusGoalFilter;

    @InjectMocks
    private GoalService goalService;


    @Test
    void getGoalsByUserId_filtered_successfully() {
        // 1. Arrange (Подготовка)
        long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto("Test Title", GoalStatus.ACTIVE);

        Goal goal1 = new Goal();
        goal1.setId(1L);
        goal1.setTitle("Test Title");
        goal1.setStatus(GoalStatus.ACTIVE);

        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Another Title");
        goal2.setStatus(GoalStatus.COMPLETED);

        Goal goal3 = new Goal();
        goal3.setId(3L);
        goal3.setTitle("Test Title");
        goal3.setStatus(GoalStatus.COMPLETED);

        List<Goal> filteredGoals = List.of(goal1);
        GoalDto goalDto1 = new GoalDto();
        goalDto1.setId(1L);
        goalDto1.setTitle("Test Title");
        goalDto1.setStatus(GoalStatus.COMPLETED);

        List<GoalDto> filteredGoalDtos = List.of(goalDto1);
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal1, goal2, goal3));
        when(goalFilters.stream()).thenReturn(Stream.empty());
        when(goalMapper.toDto(filteredGoals)).thenReturn(filteredGoalDtos);

        // 2. Act (Выполнение действия)
        List<Goal> result = goalService.getGoalsByUserId(userId, filter);

        // 3. Assert (Проверка)
        assertEquals(1, result.size());
        assertEquals(goal1.getId(), result.get(0).getId());
        assertEquals(goal1.getStatus(), result.get(0).getStatus());
        assertEquals(goal1.getTitle(), result.get(0).getTitle());

    }

    @Test
    void getGoalsByUserId_noGoalsFound_returnsEmptyList() {
        // 1. Arrange (Подготовка)
        long userId = 1L;
        GoalFilterDto filter = new GoalFilterDto("Test Title", GoalStatus.ACTIVE);
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.empty());
        when(goalFilters.stream()).thenReturn(Stream.empty());

        // 2. Act (Выполнение действия)
        List<Goal> result = goalService.getGoalsByUserId(userId, filter);

        // 3. Assert (Проверка)
        assertEquals(0, result.size());
    }

}


*/
/*    @Test
    public void testGetGoalsByUserId() {
        GoalFilterDto filter = new GoalFilterDto("Learn Java", null);
        List<Long> skillIds1 = new ArrayList<>();
        List<Long> skillIds2 = new ArrayList<>();

        Goal goal1 = new Goal(1L, "Learn Java", null, "OOP", GoalStatus.ACTIVE, skillIds1);
        GoalDto goal2 = new GoalDto(2L, "Learn Spring Boot", null, "Spring Boot Learning", GoalStatus.ACTIVE, skillIds2);
        GoalDto goal3 = new GoalDto(3L, "Learn Java", 2L, "Enum", GoalStatus.COMPLETED, skillIds2);

        when(goalRepository.findGoalsByUserId(userId1))
                .thenReturn(Stream.of(goal1, goal2, goal3));

        List<GoalDto> result = goalService.getGoalsByUserId(userId1, filter);

        assertEquals(2, result.size());
        List<String> titles = result.stream()
                .map(GoalDto::getTitle)
                .collect(Collectors.toList());

        assertEquals(List.of("Test Title", "Test Title"), titles);
    }*//*


  */
/*  @Test
    public void testGetGoalsByUserId() {
  *//*
*/
/*      User user1 = new User();
        user1.setId(userId1);
        user1.setFollowers(List.of(User.builder().id(userId2).build()));
        when(goalRepository.findGoalsByUserId(userId1)).thenReturn(user1.getGoals().stream());
        List<GoalDto> result = goalRepository.findGoalsByUserId(userId1).toList();
        assertEquals(result.get(0).getId(), userId2);*//*
*/
/*

        Goal goal1 = new Goal();
        goal1.setId(1L);
        goal1.setTitle("Title1");
        goal1.setStatus(null);

        Goal goal2 = new Goal();
        goal2.setId(2L);
        goal2.setTitle("Title");
        goal2.setStatus(null);

        Goal goal3 = new Goal();
        goal3.setId(3L);
        goal3.setTitle("Title1");
        goal3.setStatus(null);

        // Мокируем поведение репозитория
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal1, goal2, goal3));

        // Вызываем метод сервиса
        List<GoalDto> result = goalService.getGoalsByUserId(userId, titleFilter);

        // Проверяем результат
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(titleFilter.getTitle(), result.get(0).getTitle());
        assertEquals(titleFilter.getTitle(), result.get(2).getTitle());
    }*//*


*/
