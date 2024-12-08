package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import school.faang.user_service.domain.Address;
import school.faang.user_service.domain.ContactInfo;
import school.faang.user_service.domain.Education;
import school.faang.user_service.domain.Person;
import school.faang.user_service.dto.ProcessResultDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.mapper.UserContactsMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.parser.CsvParser;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.contact.ContactPreferenceService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.UserValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private final long userId = 1L;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserValidator userValidator;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private EventService eventService;
    @Mock
    private CountryService countryService;
    @Mock
    private PersonToUserMapper personToUserMapper;
    @Mock
    private CsvParser parser;
    @Mock
    private Filter<User, UserFilterDto> userNameFilter;
    @Mock
    private Filter<User, UserFilterDto> userAboutFilter;
    @Mock
    private Filter<User, UserFilterDto> userEmailFilter;
    @Mock
    private Filter<User, UserFilterDto> userContactFilter;
    @Mock
    private Filter<User, UserFilterDto> userCountryFilter;
    @Mock
    private Filter<User, UserFilterDto> userCityFilter;
    @Mock
    private Filter<User, UserFilterDto> userPhoneFilter;
    @Mock
    private Filter<User, UserFilterDto> userSkillFilter;
    @Mock
    private Filter<User, UserFilterDto> userExperienceMinFilter;
    @Mock
    private Filter<User, UserFilterDto> userExperienceMaxFilter;
    @Mock
    private UserContactsMapper userContactsMapper;
    @Mock
    private ContactPreferenceService contactPreferenceService;

    @InjectMocks
    private UserService userService;
    private User user;
    private User user1;
    private User mockUser;
    private Person mockPerson;
    private Country country1;
    private List<Event> events;
    private UserDto dto;
    private InputStream inputStream;
    private List<Person> people;


    @BeforeEach
    public void setUp() throws IOException {
        user = new User();
        user.setId(userId);
        user.setActive(true);
        user.setOwnedEvents(Arrays.asList(new Event(), new Event()));
        user.setMentees(new ArrayList<>());
        user.setSetGoals(new ArrayList<>());
        events = new ArrayList<>();
        user1 = new User();

        dto = UserDto.builder()
                .id(userId)
                .build();

        List<Filter<User, UserFilterDto>> userFilters = Arrays.asList(
                userAboutFilter,
                userCityFilter,
                userContactFilter,
                userCountryFilter,
                userEmailFilter,
                userExperienceMaxFilter,
                userExperienceMinFilter,
                userNameFilter,
                userPhoneFilter,
                userSkillFilter
        );

        userService = new UserService(
                userRepository,
                userMapper,
                personToUserMapper,
                userContactsMapper,
                userValidator,
                countryService,
                mentorshipService,
                eventService,
                contactPreferenceService,
                userFilters,
                parser
        );

        country1 = Country.builder()
                .title("Country1")
                .build();

        String testCsv = IOUtils.toString(ClassLoader.getSystemClassLoader()
                .getSystemResourceAsStream("students.csv"));
        inputStream = new ByteArrayInputStream(testCsv.getBytes());
        mockPerson = createMockPerson("John", "Doe", "john.doe@example.com");
        mockUser = createMockUser("JohnDoe", "john.doe@example.com");
        people = List.of(mockPerson);
    }

    @Test
    void checkUserExistenceWhenUserExistsShouldReturnTrue() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        assertTrue(userService.checkUserExistence(userId));

        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void checkUserExistenceWhenUserDoesNotExistShouldReturnFalse() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertFalse(userService.checkUserExistence(userId));

        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void findUserWhenUserExistsShouldReturnUser() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findUserById(userId);
        assertEquals(userId, result.getId());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findUserWhenUserDoesNotExistShouldThrowEntityNotFoundException() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.findUserById(userId));
        assertEquals(String.format("User not found by id: %s", userId), exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void deleteUserShouldCallRepositoryDeleteMethod() {
        User user = new User();
        user.setId(1L);

        userService.deleteUser(user);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void saveUserShouldCallRepositorySaveMethod() {
        User user = new User();
        user.setId(1L);

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserByIdWhenUserExistsShouldReturnUser() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
    }

    @Test
    void getUserByIdWhenUserDoesNotExistShouldReturnEmptyOptional() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(userId);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test FindById")
    void testFindByIdPositive() {
        long userId = 1L;
        User user = User.builder()
                .id(1L)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findUserById(userId);

        verify(userRepository, times(1)).findById(userId);
        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    @DisplayName("Test FindById Negative")
    void testFindByIdNegative() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> userService.findUserById(userId));
        assertEquals(String.format("User not found by id: %s", userId), exception.getMessage());
    }

    @Test
    void testDeactivateProfile_UserFound_DeactivatedSuccessful() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userValidator.isUserMentor(user)).thenReturn(true);
        when(userMapper.toDto(user)).thenReturn(new UserDto());

        UserDto result = userService.deactivateProfile(userId);

        assertNotNull(result);
        assertFalse(user.isActive());
    }

    @Test
    void testDeactivateProfile_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deactivateProfile(userId));
    }

    @Test
    void testDeactivateProfile_UserIsMentor() {
        user.getMentees().add(setUpMentee());
        long menteeId = setUpMentee().getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(new UserDto());
        when(userValidator.isUserMentor(user)).thenReturn(true);

        userService.deactivateProfile(userId);

        assertFalse(user.isActive());
        verify(mentorshipService).moveGoalsToMentee(menteeId, userId);
        verify(mentorshipService).deleteMentor(menteeId, userId);
    }

    @Test
    void testFindUserDtoById_ThrowEntityNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(userId));
    }

    @Test
    void testFindUserDtoById_Successful() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        var result = userService.findUserDtoById(userId);

        assertEquals(result.getId(), dto.getId());
    }

    private User setUpMentee() {
        User mentee = new User();
        mentee.setId(2L);
        return mentee;
    }

    @Test
    void testGetAllUsers_NoFilter() {
        Country usa = Country.builder()
                .title("USA")
                .build();

        Country canada = Country.builder()
                .title("Canada")
                .build();

        User firstUser = User.builder()
                .id(1L)
                .username("JohnDoe")
                .aboutMe("About John")
                .email("john@example.com")
                .contacts(new ArrayList<>())
                .country(usa)
                .city("New York")
                .phone("+1234567890")
                .skills(new ArrayList<>())
                .experience(5)
                .build();

        User secondUser = User.builder()
                .id(2L)
                .username("JaneSmith")
                .aboutMe("About Jane")
                .email("jane@example.com")
                .contacts(new ArrayList<>())
                .country(canada)
                .city("Toronto")
                .phone("+0987654321")
                .skills(new ArrayList<>())
                .experience(7)
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(firstUser, secondUser));

        UserDto firstUserDto = UserDto.builder()
                .id(1L)
                .username("JohnDoe")
                .build();

        UserDto secondUserDto = UserDto.builder()
                .id(2L)
                .username("JaneSmith")
                .build();

        when(userMapper.toDto(firstUser)).thenReturn(firstUserDto);
        when(userMapper.toDto(secondUser)).thenReturn(secondUserDto);

        when(userNameFilter.isApplicable(any())).thenReturn(false);
        when(userEmailFilter.isApplicable(any())).thenReturn(false);
        when(userAboutFilter.isApplicable(any())).thenReturn(false);
        when(userCityFilter.isApplicable(any())).thenReturn(false);
        when(userContactFilter.isApplicable(any())).thenReturn(false);
        when(userCountryFilter.isApplicable(any())).thenReturn(false);
        when(userExperienceMaxFilter.isApplicable(any())).thenReturn(false);
        when(userExperienceMinFilter.isApplicable(any())).thenReturn(false);
        when(userPhoneFilter.isApplicable(any())).thenReturn(false);
        when(userSkillFilter.isApplicable(any())).thenReturn(false);

        List<UserDto> result = userService.getAllUsers(UserFilterDto.builder().build());

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDto(firstUser);
        verify(userMapper, times(1)).toDto(secondUser);

        verify(userNameFilter, times(1)).isApplicable(any());
        verify(userEmailFilter, times(1)).isApplicable(any());
        verify(userAboutFilter, times(1)).isApplicable(any());
        verify(userCityFilter, times(1)).isApplicable(any());
        verify(userContactFilter, times(1)).isApplicable(any());
        verify(userCountryFilter, times(1)).isApplicable(any());
        verify(userExperienceMaxFilter, times(1)).isApplicable(any());
        verify(userExperienceMinFilter, times(1)).isApplicable(any());
        verify(userPhoneFilter, times(1)).isApplicable(any());
        verify(userSkillFilter, times(1)).isApplicable(any());

        verify(userNameFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userEmailFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userAboutFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userCityFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userContactFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userCountryFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userExperienceMaxFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userExperienceMinFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userPhoneFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userSkillFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
    }

    @Test
    void testGetAllUsers_WithSomeFilters() {
        Country usa = Country.builder().title("USA").build();

        User regularUser = User.builder()
                .id(1L)
                .username("JohnDoe")
                .aboutMe("About John")
                .email("john@example.com")
                .contacts(new ArrayList<>())
                .country(usa)
                .city("New York")
                .phone("+1234567890")
                .skills(new ArrayList<>())
                .experience(5)
                .build();

        when(userRepository.findAll()).thenReturn(Collections.singletonList(regularUser));

        UserDto firstUserDto = UserDto.builder()
                .id(1L)
                .username("JohnDoe")
                .build();

        UserDto secondUserDto = UserDto.builder()
                .id(2L)
                .username("JaneSmith")
                .build();

        when(userMapper.toDto(regularUser)).thenReturn(firstUserDto);

        UserFilterDto filterDto = UserFilterDto.builder()
                .username("John")
                .email("john@example.com")
                .build();

        when(userNameFilter.isApplicable(eq(filterDto))).thenReturn(true);
        when(userNameFilter.apply(Mockito.any(), eq(filterDto))).thenReturn(Stream.of(regularUser));

        when(userEmailFilter.isApplicable(eq(filterDto))).thenReturn(true);
        when(userEmailFilter.apply(Mockito.any(), eq(filterDto))).thenReturn(Stream.of(regularUser));

        when(userAboutFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userCityFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userContactFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userCountryFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userExperienceMaxFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userExperienceMinFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userPhoneFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userSkillFilter.isApplicable(eq(filterDto))).thenReturn(false);

        List<UserDto> result = userService.getAllUsers(filterDto);

        assertEquals(1, result.size());
        assertEquals("JohnDoe", result.get(0).getUsername());

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDto(regularUser);

        verify(userNameFilter, times(1)).isApplicable(eq(filterDto));
        verify(userEmailFilter, times(1)).isApplicable(eq(filterDto));
        verify(userAboutFilter, times(1)).isApplicable(eq(filterDto));
        verify(userCityFilter, times(1)).isApplicable(eq(filterDto));
        verify(userContactFilter, times(1)).isApplicable(eq(filterDto));
        verify(userCountryFilter, times(1)).isApplicable(eq(filterDto));
        verify(userExperienceMaxFilter, times(1)).isApplicable(eq(filterDto));
        verify(userExperienceMinFilter, times(1)).isApplicable(eq(filterDto));
        verify(userPhoneFilter, times(1)).isApplicable(eq(filterDto));
        verify(userSkillFilter, times(1)).isApplicable(eq(filterDto));

        verify(userNameFilter, times(1)).apply(Mockito.any(), eq(filterDto));
        verify(userEmailFilter, times(1)).apply(Mockito.any(), eq(filterDto));

        verify(userAboutFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userCityFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userContactFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userCountryFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userExperienceMaxFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userExperienceMinFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userPhoneFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userSkillFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
    }

    @Test
    void testGetPremiumUsers_WithSomeFilters() {

        User premiumUser = User.builder()
                .id(1L)
                .username("JohnDoe")
                .email("johndoe@example.com")
                .build();

        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(premiumUser));

        UserDto firstPremiumUserDto = UserDto.builder()
                .id(1L)
                .username("JohnDoe")
                .build();


        when(userMapper.toDto(premiumUser)).thenReturn(firstPremiumUserDto);

        UserFilterDto filterDto = UserFilterDto.builder()
                .email("johndoe@example.com")
                .build();

        when(userEmailFilter.isApplicable(eq(filterDto))).thenReturn(true);
        when(userEmailFilter.apply(Mockito.any(), eq(filterDto))).thenReturn(Stream.of(premiumUser));

        when(userAboutFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userCityFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userContactFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userCountryFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userExperienceMaxFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userExperienceMinFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userNameFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userPhoneFilter.isApplicable(eq(filterDto))).thenReturn(false);
        when(userSkillFilter.isApplicable(eq(filterDto))).thenReturn(false);

        List<UserDto> result = userService.getPremiumUsers(filterDto);

        assertEquals(1, result.size());
        assertEquals("JohnDoe", result.get(0).getUsername());

        verify(userRepository, times(1)).findPremiumUsers();
        verify(userMapper, times(1)).toDto(premiumUser);

        verify(userEmailFilter, times(1)).isApplicable(eq(filterDto));
        verify(userAboutFilter, times(1)).isApplicable(eq(filterDto));
        verify(userCityFilter, times(1)).isApplicable(eq(filterDto));
        verify(userContactFilter, times(1)).isApplicable(eq(filterDto));
        verify(userCountryFilter, times(1)).isApplicable(eq(filterDto));
        verify(userExperienceMaxFilter, times(1)).isApplicable(eq(filterDto));
        verify(userExperienceMinFilter, times(1)).isApplicable(eq(filterDto));
        verify(userNameFilter, times(1)).isApplicable(eq(filterDto));
        verify(userPhoneFilter, times(1)).isApplicable(eq(filterDto));
        verify(userSkillFilter, times(1)).isApplicable(eq(filterDto));

        verify(userEmailFilter, times(1)).apply(Mockito.any(), eq(filterDto));

        verify(userAboutFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userCityFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userContactFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userCountryFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userExperienceMaxFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userExperienceMinFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userNameFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userPhoneFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userSkillFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
    }

    @Test
    void testGetPremiumUsers_NoFilter() {
        Country usa = Country.builder()
                .title("USA")
                .build();

        User premiumUser = User.builder()
                .id(1L)
                .username("PremiumUser")
                .aboutMe("About Premium")
                .email("premium@example.com")
                .contacts(new ArrayList<>())
                .country(usa)
                .city("Los Angeles")
                .phone("+1122334455")
                .skills(new ArrayList<>())
                .experience(10)
                .build();

        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(premiumUser));

        UserDto premiumUserDto = UserDto.builder()
                .id(1L)
                .username("PremiumUser")
                .build();

        when(userMapper.toDto(premiumUser)).thenReturn(premiumUserDto);

        when(userNameFilter.isApplicable(any())).thenReturn(false);
        when(userEmailFilter.isApplicable(any())).thenReturn(false);
        when(userAboutFilter.isApplicable(any())).thenReturn(false);
        when(userCityFilter.isApplicable(any())).thenReturn(false);
        when(userContactFilter.isApplicable(any())).thenReturn(false);
        when(userCountryFilter.isApplicable(any())).thenReturn(false);
        when(userExperienceMaxFilter.isApplicable(any())).thenReturn(false);
        when(userExperienceMinFilter.isApplicable(any())).thenReturn(false);
        when(userPhoneFilter.isApplicable(any())).thenReturn(false);
        when(userSkillFilter.isApplicable(any())).thenReturn(false);

        List<UserDto> result = userService.getPremiumUsers(UserFilterDto.builder().build());

        assertEquals(1, result.size());
        assertEquals("PremiumUser", result.get(0).getUsername());
        verify(userRepository, times(1)).findPremiumUsers();
        verify(userMapper, times(1)).toDto(premiumUser);

        verify(userNameFilter, times(1)).isApplicable(any());
        verify(userEmailFilter, times(1)).isApplicable(any());
        verify(userAboutFilter, times(1)).isApplicable(any());
        verify(userCityFilter, times(1)).isApplicable(any());
        verify(userContactFilter, times(1)).isApplicable(any());
        verify(userCountryFilter, times(1)).isApplicable(any());
        verify(userExperienceMaxFilter, times(1)).isApplicable(any());
        verify(userExperienceMinFilter, times(1)).isApplicable(any());
        verify(userPhoneFilter, times(1)).isApplicable(any());
        verify(userSkillFilter, times(1)).isApplicable(any());

        verify(userNameFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userEmailFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userAboutFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userCityFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userContactFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userCountryFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userExperienceMaxFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userExperienceMinFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userPhoneFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
        verify(userSkillFilter, never()).apply(Mockito.any(), any(UserFilterDto.class));
    }

    @Test
    void testFindUser_UserExists() {
        User user = User.builder()
                .id(1L)
                .username("JohnDoe")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findUserById(1L);

        assertNotNull(result);
        assertEquals("JohnDoe", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testBanUser() {
        User user = new User();
        user.setId(1L);
        user.setBanned(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.banUser(1L);

        assertTrue(user.getBanned());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void importUsersFromCsvSuccessfully() throws IOException {
        when(parser.parseCsv(inputStream)).thenReturn(people);
        when(personToUserMapper.personToUser(mockPerson)).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        ProcessResultDto result = userService.importUsersFromCsv(inputStream);

        assertEquals(1, result.getСountSuccessfullySavedUsers());
        assertTrue(result.getErrors().isEmpty());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void importUsersFromCsvWhenSavingFails() throws Exception {
        when(parser.parseCsv(inputStream)).thenReturn(people);
        when(personToUserMapper.personToUser(mockPerson)).thenReturn(mockUser);
        when(userRepository.save(mockUser)).thenThrow(new DataIntegrityViolationException("could not execute statement; SQL [n/a]; constraint [users_phone_key] "));

        ProcessResultDto result = userService.importUsersFromCsv(inputStream);

        assertEquals(0, result.getСountSuccessfullySavedUsers());
        assertFalse(result.getErrors().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().get(0).contains("Failed to save user"));

        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void getUsersByIdsShouldReturnUserDtosWhenUsersExist() {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        List<User> users = Arrays.asList(
                User.builder().id(1L).username("John Doe").build(),
                User.builder().id(2L).username("Jane Doe").build()
        );

        List<UserDto> expectedDtos = Arrays.asList(
                UserDto.builder().id(1L).username("John Doe").build(),
                UserDto.builder().id(2L).username("Jane Doe").build()
        );

        when(userRepository.findAllById(ids)).thenReturn(users);
        when(userMapper.toDto(users)).thenReturn(expectedDtos);

        List<UserDto> result = userService.getUsersByIds(ids);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);

        verify(userRepository).findAllById(ids);
        verify(userMapper).toDto(users);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void getUsersByIdsShouldReturnEmptyListWhenNoUsersExist() {
        List<Long> ids = Arrays.asList(4L, 5L);

        when(userRepository.findAllById(ids)).thenReturn(List.of());
        when(userMapper.toDto(List.of())).thenReturn(List.of());

        List<UserDto> result = userService.getUsersByIds(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAllById(ids);
        verify(userMapper).toDto(List.of());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    private Person createMockPerson(String firstName, String lastName, String email) {
        Address address = new Address("123 Street", "New York", "NY", "Country1", "10001");
        ContactInfo contactInfo = new ContactInfo(email, "123456789", address);
        Education education = new Education("CS", 4, "SE", 3.8);

        return Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .contactInfo(contactInfo)
                .education(education)
                .employer("TechCorp")
                .build();
    }

    private User createMockUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("randomPassword");
        user.setPhone("123456789");
        return user;
    }
}
