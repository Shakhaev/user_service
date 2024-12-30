package school.faang.user_service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class UserFilterDto {
    private final String namePattern;
    private final String aboutPattern;
    private final String emailPattern;
    private final String contactPattern;
    private final String countryPattern;
    private final String cityPattern;
    private final String phonePattern;
    private final String skillPattern;
    private final int experienceMin;
    private final int experienceMax;
    private final int page;
    private final int pageSize;
}
