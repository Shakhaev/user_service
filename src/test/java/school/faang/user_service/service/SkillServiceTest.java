package school.faang.user_service.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import school.faang.user_service.mapper.SkillCandidateMapperImpl;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;

class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository guaranteeRepository;
    @Mock
    private UserService userService;
    @Mock
    private SkillValidator skillValidator;
    @Spy
    private SkillMapperImpl skillMapper;
    @Spy
    private SkillCandidateMapperImpl skillCandidateMapper;
}