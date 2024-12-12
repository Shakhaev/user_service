package school.faang.user_service.config.redis.events.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.events.PremiumBoughtEvent;

@Component
public class PremiumBoughtEventListener {

    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventMapper analyticsEventMapper;

    public PremiumBoughtEventListener(AnalyticsEventService analyticsEventService, AnalyticsEventMapper analyticsEventMapper) {
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventMapper = analyticsEventMapper;
    }

    @EventListener
    public void handlePremiumBoughtEvent(PremiumBoughtEvent event) {
        AnalyticsEvent analyticsEvent = analyticsEventMapper.map(event);
        analyticsEventService.save(analyticsEvent);
    }
}
