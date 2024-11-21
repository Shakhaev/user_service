package school.faang.user_service.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserSearchRequest;
import school.faang.user_service.dto.user.UserSearchResponse;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.search.SearchUserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserV1Controller {

    private final SearchUserService searchUserService;
    private final UserService userService;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String DEFAULT_SORT_FIELD = "averageRating";

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserSearchResponse> searchUsers(
            @RequestParam @NotBlank String sessionId,
            @RequestBody @Validated UserSearchRequest userSearchRequest,

            @PageableDefault(
                    size = DEFAULT_PAGE_SIZE,
                    sort = DEFAULT_SORT_FIELD,
                    page = 0,
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        validateRequest(userSearchRequest);

        return searchUserService.searchUsers(sessionId, userSearchRequest, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserSearchResponse getUserById(@PathVariable @Positive long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/reindex")
    @ResponseStatus(HttpStatus.OK)
    public List<UserSearchResponse> reindex() {
        return searchUserService.reindex();
    }

    private static void validateRequest(UserSearchRequest userSearchRequest) {
        if (userSearchRequest.expBoundsIsNotNull()) {
            throw new DataValidationException("experienceFrom not be greater than experienceTo");
        }
    }
}
