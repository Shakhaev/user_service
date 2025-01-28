package school.faang.user_service.controller.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.promotion.PromotionSearchService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/search/promotions")
public class SearchController {
    private final PromotionSearchService promotionSearchService;

    @GetMapping
    public List<Object> search(@RequestParam String query, @RequestParam int limit) {
        return promotionSearchService.searchResults(query,limit);
    }
}