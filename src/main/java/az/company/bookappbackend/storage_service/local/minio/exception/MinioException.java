package az.company.bookappbackend.storage_service.local.minio.exception;

public class MinioException extends RuntimeException {
    public MinioException(String message) {
        super(message);
    }
}
