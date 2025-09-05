package az.company.bookappbackend.storage_service.local.minio.exception;

public class FileDeleteException extends RuntimeException {
    public FileDeleteException(String message) {
        super(message);
    }
}
