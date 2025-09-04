package az.company.bookappbackend.minio.exceptions;

public class FileDeleteException extends RuntimeException {
    public FileDeleteException(String message) {
        super(message);
    }
}
