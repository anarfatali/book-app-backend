package az.company.bookappbackend.minio.service;

import az.company.bookappbackend.minio.exceptions.FileFetchException;
import az.company.bookappbackend.minio.exceptions.FileUploadException;
import az.company.bookappbackend.minio.exceptions.FileValidationException;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioStorageService {

    private final MinioClient minioClient;
    private static final String BUCKET_NAME = "profile-photos";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileValidationException("File cannot be empty.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("File size exceeds the limit of 5MB.");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new FileValidationException("Invalid file type. Only JPEG, PNG, and GIF are allowed.");
        }
    }

    public GetObjectResponse getProfilePhotoFile(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Couldn't get the file. Message: {}", e.getMessage());
            throw new FileFetchException(e.getMessage());
        }
    }

    public String uploadProfilePhoto(String username, MultipartFile file) {
        validateFile(file);

        // This code gets file extension
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.lastIndexOf('.') != -1) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        String objectName = username + fileExtension;

        // The actual file upload to MiniO
        // This overrides old file if the file exists. Otherwise, creates new
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            log.error("Couldn't write the file. Message: {}", e.getMessage());
            throw new FileUploadException(e.getMessage());
        }

        return objectName;
    }

}
