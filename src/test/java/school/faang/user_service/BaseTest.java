package school.faang.user_service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.data.CountryData;
import school.faang.user_service.data.SkillData;
import school.faang.user_service.data.UserData;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

@SpringBootTest
public class BaseTest {
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected CountryRepository countryRepository;
    @Autowired
    protected SkillRepository skillRepository;
    @Autowired
    protected RecommendationRequestRepository recommendationRequestRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    protected Country createCountry(CountryData countryData) {
        return countryRepository.save(countryData.toCountry());
    }

    protected User createUser(UserData userData) {
        return userRepository.save(userData.getUser());
    }

    protected User createUser(User user) {
        return userRepository.save(user);
    }

    protected Skill createSkill(SkillData skillData) {
        return skillRepository.save(skillData.toSkill());
    }
}
