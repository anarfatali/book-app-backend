package az.company.bookappbackend.minio.exceptions;

public class FileFetchException extends RuntimeException {
    public FileFetchException(String message) {
        super(message);
    }
}
