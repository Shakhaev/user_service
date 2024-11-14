package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.client.PromotionClient;
import school.faang.user_service.client.dto.ResourceDocumentResponseDto;
import school.faang.user_service.dto.user.UserSearchResponseDto;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.model.jpa.User;
import school.faang.user_service.model.search.user.UserDocument;
import school.faang.user_service.model.search.user.UserFilter;
import school.faang.user_service.repository.jpa.UserRepository;
import school.faang.user_service.repository.search.UserDocumentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final double SHARE_OF_PROMOTIONS = 0.4;

    private final UserRepository userRepo;
    private final PromotionClient promotionClient;
    private final UserDocumentRepository userDocumentRepository;

    public User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public List<User> getAllUsersByIds(List<Long> ids) {
        return userRepo.findAllById(ids);
    }

    public Page<UserSearchResponseDto> searchUsers(String sessionId, UserFilter userFilter, Pageable pageable) {
        Integer requiredPromotionsCount = (int) Math.floor(pageable.getPageSize() * 0.4);
        List<ResourceDocumentResponseDto> promotionResources = promotionClient.getPromotions(requiredPromotionsCount, sessionId);
        Integer remainingPositionsCount = pageable.getPageSize() - promotionResources.size();

        List<Long> promotedResourceIds = promotionResources.stream()
                .map(ResourceDocumentResponseDto::resourceId)
                .toList();

        List<UserDocument> promotedUserDocs = userDocumentRepository.findAllByUserIdIn(promotedResourceIds);

    }
}