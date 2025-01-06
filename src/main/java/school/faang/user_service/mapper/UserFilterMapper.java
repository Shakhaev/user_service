package school.faang.user_service.mapper;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.UserFilter;

@Component
public class UserFilterMapper {
    public UserFilter toEntity(UserFilterDto dto) {
        UserFilter entity = new UserFilter();
        entity.setNamePattern(dto.getNamePattern());
        entity.setAboutPattern(dto.getAboutPattern());
        entity.setEmailPattern(dto.getEmailPattern());
        entity.setContactPattern(dto.getContactPattern());
        entity.setCountryPattern(dto.getCountryPattern());
        entity.setCityPattern(dto.getCityPattern());
        entity.setPhonePattern(dto.getPhonePattern());
        entity.setSkillPattern(dto.getSkillPattern());
        entity.setExperienceMin(dto.getExperienceMin());
        entity.setExperienceMax(dto.getExperienceMax());
        entity.setPage(dto.getPage());
        entity.setPageSize(dto.getPageSize());
        return entity;
    }
}
