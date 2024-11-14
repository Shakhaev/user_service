package school.faang.user_service.client.dto;

public record ResourceDocumentResponseDto(
        Long resourceId,
        String resourceType,
        Long ownerId
) {
}
