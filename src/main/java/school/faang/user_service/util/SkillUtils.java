package school.faang.user_service.util;

import school.faang.user_service.model.jpa.Skill;
import school.faang.user_service.model.jpa.recommendation.Recommendation;
import school.faang.user_service.model.jpa.recommendation.SkillOffer;

import java.util.List;

public class SkillUtils {

    public static List<Skill> toSkillList(List<SkillOffer> skillOffers) {
        return skillOffers.stream()
                .map(SkillOffer::getSkill)
                .toList();
    }

    public static List<Skill> getSkillsFromRecommendations(List<Recommendation> otherRecommendation) {
        return otherRecommendation.stream()
                .flatMap(currRecommendation -> currRecommendation.getSkillOffers().stream())
                .map(SkillOffer::getSkill)
                .toList();
    }
}
