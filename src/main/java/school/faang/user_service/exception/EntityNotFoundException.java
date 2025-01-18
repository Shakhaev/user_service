package school.faang.user_service.exception;

/**
 * Исключение, выбрасываемое в случае, когда сущность не найдена в системе.
 * Это исключение наследует {@link RuntimeException} и используется для
 * сигнализации о том, что запрашиваемая сущность отсутствует.
 *
 * Обычно это исключение применяется, когда операция требует существования
 * сущности (например, поиск по ID), но сущность не была найдена.
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
