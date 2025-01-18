package school.faang.user_service.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MessageError {
    TITLE_BLANK("Название скила не может быть пустым"),
    SKILL_TITLE_EXIST("Скилл с таким названием уже существует"),
    USER_ALREADY_HAS_SUGGESTED_SKILL("У пользователя уже есть предложенный скил"),
    NOT_ENOUGH_SKILL_OFFERS("Недостаточно предложений для получения скила"),
    SKILL_NOT_AVAILABLE("Невозможно получить скилл");

    private final String message;
}
