package school.faang.user_service.dto;

public record UserFilterDto(String namePattern, String aboutPattern, String emailPattern, String contactPattern,
                            String countryPattern, String cityPattern, String phonePattern, String skillPattern,
                            int experienceMin, int experienceMax, int page, int pageSize) {
}
