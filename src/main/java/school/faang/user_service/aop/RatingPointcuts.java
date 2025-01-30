package school.faang.user_service.aop;

import org.aspectj.lang.annotation.Pointcut;

public class RatingPointcuts {

    @Pointcut("execution(* school.faang.user_service.service.SubscriptionService.followUser(..))")
    public void followUser() {
    }

    @Pointcut("execution(* school.faang.user_service.service.SubscriptionService.unfollowUser(..))")
    public void unfollowUser() {
    }

    @Pointcut("execution(* school.faang.user_service.service.SkillService.acquireSkillFromOffers(..))")
    public void acquireSkillFromOffers() {
    }

    @Pointcut("execution(* school.faang.user_service.service.SkillService.create(..))")
    public void createSkill() {
    }


}
