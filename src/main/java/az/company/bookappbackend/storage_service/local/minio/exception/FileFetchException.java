package az.company.bookappbackend.storage_service.local.minio.exception;

public class FileFetchException extends RuntimeException {
    public FileFetchException(String message) {
        super(message);
    }
}
