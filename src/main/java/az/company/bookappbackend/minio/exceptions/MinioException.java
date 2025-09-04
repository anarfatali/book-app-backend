package az.company.bookappbackend.minio.exceptions;

public class MinioException extends RuntimeException {
    public MinioException(String message) {
        super(message);
    }
}
