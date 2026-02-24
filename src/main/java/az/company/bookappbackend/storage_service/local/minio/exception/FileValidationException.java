package az.company.bookappbackend.storage_service.local.minio.exception;

public class FileValidationException extends RuntimeException {
    public FileValidationException(String message) {
        super(message);
    }
}
