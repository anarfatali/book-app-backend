package az.company.bookappbackend.storage_service.remote.amazon.s3.exception;

public class S3FileDeleteException extends RuntimeException {
    public S3FileDeleteException(String message) {
        super(message);
    }
}
