package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.validator.RecommendationRequestFilter;
import school.faang.user_service.service.validator.RecommendationRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationRequestServiceTest {

    @InjectMocks
    private RecommendationRequestService service;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private RecommendationRequestValidator validator;

    @Mock
    private RecommendationRequestFilter recommendationRequestFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate_Success() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);
        requestDto.setSkills(new ArrayList<>());

        User requester = new User();
        User receiver = new User();
        RecommendationRequest requestEntity = new RecommendationRequest();
        RecommendationRequest savedRequestEntity = new RecommendationRequest();
        RecommendationRequestDto savedDto = new RecommendationRequestDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestMapper.toEntity(requestDto)).thenReturn(requestEntity);
        when(recommendationRequestRepository.save(requestEntity)).thenReturn(savedRequestEntity);
        when(recommendationRequestMapper.toDto(savedRequestEntity)).thenReturn(savedDto);

        RecommendationRequestDto result = service.create(requestDto);
        assertNotNull(result);
        assertEquals(savedDto, result);
        verify(skillRequestRepository, times(requestDto.getSkills().size())).create(anyLong(), anyLong());
    }

    @Test
    void testCreate_UserNotFound() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.create(requestDto));
    }

    @Test
    void testCreate_SaveFails() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);

        User requester = new User();
        User receiver = new User();
        RecommendationRequest requestEntity = new RecommendationRequest();

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestMapper.toEntity(requestDto)).thenReturn(requestEntity);
        when(recommendationRequestRepository.save(requestEntity))
                .thenThrow(new DataAccessException("DB error") {});

        assertThrows(DataAccessException.class, () -> service.create(requestDto));
        verify(userRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(recommendationRequestMapper).toEntity(requestDto);
        verify(recommendationRequestRepository).save(requestEntity);
        verifyNoInteractions(skillRequestRepository);
    }

    @Test
    void testGetRequest_Success() {
        long requestId = 1L;
        RecommendationRequest entity = new RecommendationRequest();
        RecommendationRequestDto dto = new RecommendationRequestDto();

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(entity));
        when(recommendationRequestMapper.toDto(entity)).thenReturn(dto);

        RecommendationRequestDto result = service.getRequest(requestId);

        assertNotNull(result);
        assertEquals(dto, result);
    }

    @Test
    void testGetRequest_NotFound() {
        long requestId = 1L;

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getRequest(requestId));
    }

    @Test
    void testRejectRequest_Success() {
        long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setRejectionReason("...");

        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.PENDING);

        RecommendationRequest updatedRequest = new RecommendationRequest();
        updatedRequest.setStatus(RequestStatus.REJECTED);

        RecommendationRequestDto dto = new RecommendationRequestDto();

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(recommendationRequestRepository.save(request)).thenReturn(updatedRequest);
        when(recommendationRequestMapper.toDto(updatedRequest)).thenReturn(dto);

        RecommendationRequestDto result = service.rejectRequest(requestId, rejectionDto);

        assertNotNull(result);
        assertEquals(dto, result);
    }


    @Test
    void testRejectRequest_AlreadyProcessed() {
        long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto();

        RecommendationRequest request = new RecommendationRequest();
        request.setStatus(RequestStatus.REJECTED);

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThrows(BusinessException.class, () -> service.rejectRequest(requestId, rejectionDto));
    }

    @Test
    void testGetRequests_Success() {
        RequestFilterDto filter = new RequestFilterDto();
        List<RecommendationRequest> allRequests = List.of(new RecommendationRequest());
        List<RecommendationRequest> filteredRequests = List.of(new RecommendationRequest());
        RecommendationRequestDto dto = new RecommendationRequestDto();

        when(recommendationRequestRepository.findAll()).thenReturn(allRequests);
        when(recommendationRequestFilter.filterRequests(allRequests, filter)).thenReturn(filteredRequests);
        when(recommendationRequestMapper.toDto(any())).thenReturn(dto);

        List<RecommendationRequestDto> result = service.getRequests(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }
}
