package school.faang.user_service.service.decorators.skill;

import org.springframework.stereotype.Service;
import school.faang.user_service.config.AppConfig;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.rating.ActionType;
import school.faang.user_service.service.RatingService;
import school.faang.user_service.service.SkillService;

@Service
public class RatingSkillServiceDecorator extends SkillServiceDecorator {
    private final RatingService ratingService;
    private final AppConfig appConfig;

    public RatingSkillServiceDecorator(
            SkillService skillService,
            RatingService ratingService,
            AppConfig appConfig) {
        super(skillService);
        this.ratingService = ratingService;
        this.appConfig = appConfig;
    }

    @Override
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        addRating(userId);
        return super.acquireSkillFromOffers(skillId, userId);
    }

    private void addRating(long userId) {
        ratingService.addRating(
                u -> "User : " + u.getUsername() + " -> acquired skill and got rating!",
                userId,
                appConfig.getActiveTransaction(),
                ActionType.ACTIVE
        );
    }
}
