package az.company.bookappbackend.storage_service;

public record FileContent(
        String contentType,
        byte[] fileBytes
) {
}
