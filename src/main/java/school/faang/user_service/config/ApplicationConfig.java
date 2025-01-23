package school.faang.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.filters.interfaces.UserFilter;
import school.faang.user_service.filters.subscription.*;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    @Bean
    public List<UserFilter> userFilters(AboutFilter aboutFilter,
                                        CityFilter cityFilter,
                                        CountryFilter countryFilter,
                                        EmailFilter emailFilter,
                                        ExperienceFilter experienceFilter,
                                        NameFilter nameFilter,
                                        PhoneFilter phoneFilter,
                                        SkillFilter skillFilter) {
        return List.of(cityFilter, nameFilter, aboutFilter, countryFilter, emailFilter, experienceFilter, phoneFilter, skillFilter);
    }

}
