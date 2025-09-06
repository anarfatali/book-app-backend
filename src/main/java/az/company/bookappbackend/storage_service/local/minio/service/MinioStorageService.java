package az.company.bookappbackend.storage_service.local.minio.service;

import az.company.bookappbackend.storage_service.FileContent;
import az.company.bookappbackend.storage_service.FileUtility;
import az.company.bookappbackend.storage_service.StorageService;
import az.company.bookappbackend.storage_service.local.minio.exception.FileDeleteException;
import az.company.bookappbackend.storage_service.local.minio.exception.FileUploadException;
import az.company.bookappbackend.storage_service.local.minio.exception.FileValidationException;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;
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
            throw new FileValidationException("Invalid file type. Only JPEG, PNG, GIF and WEBP are allowed.");
        }
    }

    @Override
    public String uploadFile(String fileName, String bucketName, MultipartFile file) {
        validateFile(file);

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return "success";
        } catch (Exception e) {
            log.error("Couldn't write the file. Message: {}", e.getMessage());
            throw new FileUploadException(e.getMessage());
        }
    }

    @Override
    public String uploadFile(String filename, String bucketName, byte[] file, String contentType) {
        try {
            try (InputStream inputStream = new ByteArrayInputStream(file)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(filename)
                                .stream(inputStream, file.length, -1)
                                .contentType(contentType)
                                .build()
                );
            }

            return "success";
        } catch (Exception e) {
            log.error("Couldn't write the file with bytes. Message: {}", e.getMessage());
            throw new FileUploadException(e.getMessage());
        }
    }

    @Override
    public Optional<FileContent> findFile(String fileName, String bucketName) {
        try (
                GetObjectResponse response = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .build()
                )
        ) {

            String contentType = response.headers().get("Content-Type");

            //minio can sometimes return not a correct content type
            if (contentType == null || contentType.isEmpty()) {
                contentType = FileUtility.determineContentTypeFromFileName(fileName);
            }

            FileContent fileContent = new FileContent(
                    contentType,
                    response.readAllBytes()
            );

            return Optional.of(fileContent);
        } catch (Exception e) {
            log.error("Couldn't get the file. Message: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void deleteFile(String fileName, String bucketName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            log.error("Couldn't delete the file. Message: {}", e.getMessage());
            throw new FileDeleteException(e.getMessage());
        }
    }
}
