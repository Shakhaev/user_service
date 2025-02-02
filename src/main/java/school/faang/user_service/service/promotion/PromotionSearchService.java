package school.faang.user_service.service.promotion;

import java.util.List;

public interface PromotionSearchService {
    List<Object> searchResults(String query, int limit);
}