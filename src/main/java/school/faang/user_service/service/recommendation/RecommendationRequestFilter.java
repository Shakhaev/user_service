package school.faang.user_service.service.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
public class RecommendationRequestFilter {

    public List<Predicate<RecommendationRequest>> getPredicates(RequestFilterDto filter) {
        List<Predicate<RecommendationRequest>> predicates = new ArrayList<>();
        addPredicate(filter.requesterId(), predicates,
                requesterId ->
                        request -> request.getRequester().getId().equals(requesterId));
        addPredicate(filter.receiverId(), predicates, receiverId ->
                request -> request.getReceiver().getId().equals(receiverId));
        addPredicate(filter.message(), predicates,
                message -> request ->
                        request.getMessage().contains(message));
        addPredicate(filter.status(), predicates,
                status -> request ->
                        request.getStatus().equals(status));
        addPredicate(filter.rejectionReason(), predicates,
                rejectionReason ->
                        request -> request.getRejectionReason().contains(rejectionReason));
        addPredicate(filter.recommendationId(), predicates,
                recommendationId ->
                        request -> request.getRecommendation().getId() == recommendationId);
        addPredicate(filter.createdAtFrom(), predicates,
                createdAtFrom ->
                        request -> !request.getCreatedAt().isBefore(createdAtFrom));
        addPredicate(filter.createdAtTo(), predicates,
                createdAtTo ->
                        request -> !request.getCreatedAt().isAfter(createdAtTo));
        addPredicate(filter.updatedAtFrom(), predicates,
                updatedAtFrom ->
                        request -> !request.getUpdatedAt().isBefore(updatedAtFrom));
        addPredicate(filter.updatedAtTo(), predicates,
                updatedAtTo ->
                        request -> !request.getUpdatedAt().isAfter(updatedAtTo));
        return predicates;
    }

    private static <T> void addPredicate(T value,
                                         List<Predicate<RecommendationRequest>> predicates,
                                         Function<T, Predicate<RecommendationRequest>> predicateSupplier) {
        if(value != null) {
            predicates.add(predicateSupplier.apply(value));
        }
    }
}
