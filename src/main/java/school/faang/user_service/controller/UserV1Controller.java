package school.faang.user_service.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.model.search.user.UserFilter;
import school.faang.user_service.dto.user.UserSearchResponseDto;
import school.faang.user_service.model.jpa.Skill;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserV1Controller {

    private final UserService userService;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String DEFAULT_SORT_FIELD = "rating";

    public ResponseEntity<Page<UserSearchResponseDto>> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<String> skillNames,
            @RequestParam(required = false) Integer experienceFrom,
            @RequestParam(required = false) Integer experienceTo,

            HttpSession session,

            @PageableDefault(
                    size = DEFAULT_PAGE_SIZE,
                    sort = DEFAULT_SORT_FIELD,
                    page = 0,
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        if (experienceFrom > experienceTo) {
            throw new DataValidationException("experienceFrom not be greater than experienceTo");
        }
        UserFilter filter = new UserFilter(query, skillNames, experienceFrom, experienceTo);
        Page<UserSearchResponseDto> searchResponse = userService.searchUsers(session.getId(), filter, pageable);
        return ResponseEntity.ok(searchResponse);
    }
}
