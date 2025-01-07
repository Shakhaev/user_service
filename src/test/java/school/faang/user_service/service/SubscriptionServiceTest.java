package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SubscriptionUserMapperImpl;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.ArrayList;
import java.util.List;

import static school.faang.user_service.exception.MessageError.USER_ALREADY_HAS_THIS_FOLLOWER;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    @Mock
    SubscriptionRepository subscriptionRepository;
    @Spy
    SubscriptionUserMapperImpl subscriptionUserMapper;
    @InjectMocks
    SubscriptionService subscriptionService;
    long followerId;
    long followeeId;
    List<User> followers = new ArrayList<>();
    List<User> followees = new ArrayList<>();
    SubscriptionUserFilterDto filter;
    List<SubscriptionUserDto> filteredUsersDtos;
    List<SubscriptionUserDto> expectedUserDtos;

    @BeforeEach
    public void init() {
        followerId = 1L;
        followeeId = 2L;
        fillFollowersAndFollowees();
        filter = new SubscriptionUserFilterDto();
    }

    @Test
    @DisplayName("Follow To Another User")
    void testFollowOneUserByAnotherUser() {
        subscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Follow To Himself")
    void testFollowUserByHimself() {
        Mockito.when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followerId))
                .thenThrow(new DataValidationException(USER_ALREADY_HAS_THIS_FOLLOWER));
        Assert.assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followerId));
    }

    @Test
    @DisplayName("Unfollow Another User")
    void testUnfollowOneUserFromAnotherUser() {
        subscriptionService.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    @DisplayName("Get Followers Count")
    void testGetFollowersCount() {
        subscriptionService.getFollowersCount(followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFollowersAmountByFolloweeId(followeeId);
    }

    @Test
    @DisplayName("Get Following Count")
    void testGetFollowingCount() {
        subscriptionService.getFollowingCount(followerId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFolloweesAmountByFollowerId(followerId);
    }

    @Test
    @DisplayName("Test user filter by Name")
    void testFilterFollowersByName() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setNamePattern("m.sha");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() != 3L)
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Name")
    void testFilterFollowersByEmptyName() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setNamePattern("");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by About Me")
    void testFilterFollowersByAboutMe() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setAboutPattern("I'm Masha");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 2L)
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Email")
    void testFilterFollowersByEmptyEmail() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setEmailPattern("");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Email")
    void testFilterFollowersByEmail() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setEmailPattern("misha@mail.ru");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 1L)
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty About Me")
    void testFilterFollowersByEmptyAboutMe() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());


        filter.setAboutPattern(null);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by City")
    void testFilterFollowersByCity() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setCityPattern("Moscow");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 1L)
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty City")
    void testFilterFollowersByEmptyCity() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setCityPattern(null);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Phone")
    void testFilterFollowersByPhone() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setPhonePattern("456");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 2L)
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Phone")
    void testFilterFollowersByEmptyPhone() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setCityPattern(null);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Country")
    void testFilterFollowersByCountry() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setCountryPattern("China");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 3L)
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Country")
    void testFilterFollowersByEmptyCountry() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setSkillPattern(null);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Skills")
    void testFilterFollowersByEmptySkills() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setCityPattern(null);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by 1 of Skills")
    void testFilterFollowersByOneSkills() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setSkillPattern("Skill 1");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 1L)
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by 2 of Skills")
    void testFilterFollowersByTwoSkills() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setSkillPattern("Skill 3");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> (u.getId() == 2L || u.getId() == 3L))
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Contacts")
    void testFilterFollowersByEmptyContacts() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setContactPattern(null);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by 1 of Contacts")
    void testFilterFollowersByOneContact() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setContactPattern("Contact 1");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> u.getId() == 1L)
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by 2 of Contacts")
    void testFilterFollowersByTwoContacts() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setContactPattern("Contact 3");
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> (u.getId() == 2L || u.getId() == 3L))
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Empty Experiences")
    void testFilterFollowersByEmptyExperiences() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setExperienceMin(0);
        filter.setExperienceMax(0);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    @Test
    @DisplayName("Test user filter by Experiences")
    void testFilterFollowersByExperiences() {
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId)).thenReturn(followers.stream());

        filter.setExperienceMin(15);
        filter.setExperienceMax(40);
        filteredUsersDtos = subscriptionService.getFollowing(followeeId, filter);

        expectedUserDtos = followers.stream()
                .filter(u -> (u.getId() == 2L || u.getId() == 3L))
                .map(subscriptionUserMapper::toDto)
                .toList();

        Assertions.assertArrayEquals(expectedUserDtos.toArray(), filteredUsersDtos.toArray());
    }

    private void fillFollowersAndFollowees() {
        Country countryRussia = Country.builder().id(1).title("Russia").build();
        Country countryUsa = Country.builder().id(1).title("USA").build();
        Country countryChina = Country.builder().id(1).title("China").build();

        Skill skill1 = Skill.builder().id(1).title("Skill 1").build();
        Skill skill2 = Skill.builder().id(1).title("Skill 2").build();
        Skill skill3 = Skill.builder().id(1).title("Skill 3").build();

        List<Skill> skillSet1 = new ArrayList<>(List.of(skill1));
        List<Skill> skillSet23 = new ArrayList<>(List.of(skill2, skill3));
        List<Skill> skillSet3 = new ArrayList<>(List.of(skill3));

        Contact contact1 = Contact.builder().contact("Contact 1").build();
        Contact contact2 = Contact.builder().contact("Contact 2").build();
        Contact contact3 = Contact.builder().contact("Contact 3").build();

        List<Contact> contacts1 = new ArrayList<>(List.of(contact1));
        List<Contact> contacts23 = new ArrayList<>(List.of(contact2, contact3));
        List<Contact> contacts3 = new ArrayList<>(List.of(contact3));

        User user1 = User.builder()
                .city("Moscow")
                .active(true)
                .id(1L)
                .country(new Country(1L, "Country 1", new ArrayList<>()))
                .goals(new ArrayList<>())
                .contacts(contacts1)
                .aboutMe("I'm Misha")
                .email("misha@mail.ru")
                .username("misha")
                .phone("123")
                .country(countryRussia)
                .skills(skillSet1)
                .experience(10)
                .build();
        followers.add(user1);
        followees.add(user1);

        User user2 = User.builder()
                .city("Piter")
                .active(false)
                .id(2L)
                .country(new Country(2L, "Country 2", new ArrayList<>()))
                .goals(new ArrayList<>())
                .contacts(contacts23)
                .aboutMe("I'm Masha")
                .email("masha@mail.ru")
                .username("masha")
                .phone("456")
                .country(countryUsa)
                .skills(skillSet23)
                .experience(20)
                .build();

        followers.add(user2);
        followees.add(user2);

        User user3 = User.builder()
                .city("Kazan")
                .active(true)
                .id(3L)
                .country(new Country(3L, "Country 3", new ArrayList<>()))
                .goals(new ArrayList<>())
                .contacts(contacts3)
                .aboutMe("I'm Kesha")
                .email("kesha@mail.ru")
                .username("kesha")
                .phone("789")
                .country(countryChina)
                .skills(skillSet3)
                .experience(30)
                .build();

        followers.add(user3);
        followees.add(user3);
    }
}
