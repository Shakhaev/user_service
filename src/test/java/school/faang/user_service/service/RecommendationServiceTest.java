package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.CreateRecommendationResponse;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequest;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private RecommendationValidator recommendationValidator;

    @Spy
    private RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);

    @Captor
    private ArgumentCaptor<UserSkillGuarantee> captor;

    @Test
    public void create_ShouldThrowDataValidationExceptionWhenSkillDoesNotExist() {
        CreateRecommendationRequest createRequest = new CreateRecommendationRequest();
        createRequest.setAuthorId(1L);
        createRequest.setReceiverId(2L);
        createRequest.setContent("content");
        createRequest.setSkillIds(List.of(1L, 7L));
        createRequest.setCreatedAt(LocalDateTime.now());

        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(7L)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> recommendationService.create(createRequest));
    }

    @Test
    public void create_ShouldCreateRecommendationSuccessfully() {
        CreateRecommendationRequest createRequest = new CreateRecommendationRequest();
        createRequest.setAuthorId(1L);
        createRequest.setReceiverId(2L);
        createRequest.setContent("content");
        createRequest.setSkillIds(List.of(1L, 2L));
        createRequest.setCreatedAt(LocalDateTime.now());

        when(skillRepository.existsById(1L)).thenReturn(true);
        when(skillRepository.existsById(2L)).thenReturn(true);

        User author = new User();
        User receiver = new User();
        author.setId(1L);
        receiver.setId(2L);

        when(userRepository.getReferenceById(1L)).thenReturn(author);
        when(userRepository.getReferenceById(2L)).thenReturn(receiver);

        Skill firstSkill = new Skill();
        Skill secondSkill = new Skill();
        firstSkill.setId(1L);
        secondSkill.setId(2L);

        when(skillRepository.findAllById(createRequest.getSkillIds())).thenReturn(List.of(firstSkill, secondSkill));

        when(recommendationRepository.create(author.getId(), receiver.getId(), createRequest.getContent())).thenReturn(1L);
        when(skillOfferRepository.create(firstSkill.getId(), 1L)).thenReturn(1L);
        when(skillOfferRepository.create(secondSkill.getId(), 1L)).thenReturn(2L);

        SkillOffer firstSkillOffer = new SkillOffer();
        SkillOffer secondSkillOffer = new SkillOffer();
        firstSkillOffer.setId(1L);
        secondSkillOffer.setId(2L);

        when(skillOfferRepository.findById(1L)).thenReturn(Optional.of(firstSkillOffer));
        when(skillOfferRepository.findById(2L)).thenReturn(Optional.of(secondSkillOffer));

        CreateRecommendationResponse createResponse = recommendationService.create(createRequest);

        verify(recommendationRepository, times(1)).create(author.getId(), receiver.getId(), createRequest.getContent());
        verify(userSkillGuaranteeRepository, times(2)).save(captor.capture());
        verify(skillOfferRepository, times(1)).create(firstSkill.getId(), 1L);
        verify(skillOfferRepository, times(1)).create(secondSkill.getId(), 1L);

        assertEquals(1L, createResponse.getId());
        assertEquals(1L, createResponse.getAuthorId());
        assertEquals(2L, createResponse.getReceiverId());
        assertEquals("content", createResponse.getContent());
        assertEquals(1L, createResponse.getSkillOfferIds().get(0));
        assertEquals(2L, createResponse.getSkillOfferIds().get(1));
        assertEquals(createRequest.getCreatedAt(), createResponse.getCreatedAt());

    }


}
