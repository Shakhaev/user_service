package school.faang.user_service.dto;

import java.time.LocalDateTime;

public record ProfileViewEvent(long idRequester, long idUser, LocalDateTime createdTime) {
}
