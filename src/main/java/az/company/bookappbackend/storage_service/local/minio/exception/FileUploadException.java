package az.company.bookappbackend.storage_service.local.minio.exception;

public class FileUploadException extends RuntimeException {
    public FileUploadException(String message) {
        super(message);
    }
}
