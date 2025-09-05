package az.company.bookappbackend.storage_service.remote.amazon.s3.exception;

public class S3FileUploadException extends RuntimeException {
    public S3FileUploadException(String message) {
        super(message);
    }
}
