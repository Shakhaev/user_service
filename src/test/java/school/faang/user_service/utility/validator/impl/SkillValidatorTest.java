package school.faang.user_service.utility.validator.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MinSkillOffersException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.utility.validator.SkillValidator;


import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {
    private static final long USER_ID = 1L;
    private static final long SKILL_ID = 1L;
    private static final int MIN_SKILL_OFFERS = 3;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillValidator skillValidator;
    private User user;
    private Skill skill;

    @BeforeEach
    public void beforeEach() {

        user = new User();
        user.setId(USER_ID);
        user.setUsername("Alex");

        skill = new Skill();
        skill.setId(SKILL_ID);
        skill.setTitle("Java");
    }

    @Test
    void validateSkill_validSkill_doesNotThrowException() {
        when(skillRepository.existsByTitle("Java")).thenReturn(false);
        assertDoesNotThrow(() -> skillValidator.validateSkill(skill));
        verify(skillRepository).existsByTitle("Java");
    }

    @Test
    void validateSkill_nullTitle_throwsIllegalArgumentException() {
        Skill skillWithNullTitle = new Skill();
        skillWithNullTitle.setTitle(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> skillValidator.validateSkill(skillWithNullTitle));
        assertEquals("Title cannot be null or empty !", exception.getMessage());
        verify(skillRepository, never()).existsByTitle(anyString());
    }

    @Test
    void validateSkill_emptyTitle_throwsIllegalArgumentException() {
        Skill skillWithEmptyTitle = new Skill();
        skillWithEmptyTitle.setTitle("");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> skillValidator.validateSkill(skillWithEmptyTitle));
        assertEquals("Title cannot be null or empty !", exception.getMessage());
        verify(skillRepository, never()).existsByTitle(anyString());
    }

    @Test
    void validateSkill_existingTitle_throwsDataValidationException() {
        when(skillRepository.existsByTitle("Java")).thenReturn(true);
        assertThrows(DataValidationException.class, () -> skillValidator.validateSkill(skill));
        verify(skillRepository).existsByTitle("Java");
    }

    @Test
    void validateSkillOffers_validOffers_doesNotThrowException() {
        List<SkillOffer> offers = Arrays.asList(new SkillOffer(), new SkillOffer(), new SkillOffer());
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        assertDoesNotThrow(() -> skillValidator.validateSkillOffers(offers, 1L, 1L));
    }

    @Test
    void validateSkillOffers_notEnoughOffers_throwsMinSkillOffersException() {
        List<SkillOffer> offers = Arrays.asList(new SkillOffer(), new SkillOffer());
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        MinSkillOffersException exception = assertThrows(MinSkillOffersException.class,
                () -> skillValidator.validateSkillOffers(offers, 1L, 1L));
        assertEquals(" Test Skill skill not assigned, 3 is needed instead of 2", exception.getMessage());
    }

    @Test
    void getUserById_validUserId_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = skillValidator.getUserById(1L);
        assertEquals(user, result);
    }

    @Test
    void getUserById_invalidUserId_throwsNoSuchElementException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> skillValidator.getUserById(1L));
        assertEquals("User with ID 1 not found", exception.getMessage());
    }

    @Test
    void existingSkillIsPresent_skillDoesNotExist_doesNotThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        assertDoesNotThrow(() -> skillValidator.existingSkillIsPresent(Optional.empty(), 1L, 1L));
    }

    @Test
    void existingSkillIsPresent_skillExists_throwsMinSkillOffersException() {
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        MinSkillOffersException exception = assertThrows(MinSkillOffersException.class, () -> skillValidator.existingSkillIsPresent(Optional.of(skill), 1L, 1L));
        assertEquals("The assignment of the skill was rejected because the skill Test Skill  already exists in the user testuser", exception.getMessage());
    }
}