package az.company.bookappbackend.storage_service.remote.amazon.s3.service;

import az.company.bookappbackend.storage_service.FileContent;
import az.company.bookappbackend.storage_service.StorageService;
import az.company.bookappbackend.storage_service.remote.amazon.s3.exception.S3FileDeleteException;
import az.company.bookappbackend.storage_service.remote.amazon.s3.exception.S3FileUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("prod") // for application-prod.yaml profile
public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    @Override
    public String uploadFile(String fileName, String bucketName, MultipartFile file) {
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return "Successfully uploaded file to S3";
        } catch (Exception e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            throw new S3FileUploadException("Failed to upload file to S3");
        }
    }

    @Override
    public Optional<FileContent> findFile(String fileName, String bucketName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);

            byte[] bytes = objectBytes.asByteArray();
            String contentType = objectBytes.response().contentType();

            return Optional.of(new FileContent(contentType, bytes));
        } catch (Exception e) {
            log.error("Couldn't get the file. Message: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void deleteFile(String fileName, String bucketName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("Couldn't delete the file. Message: {}", e.getMessage());
            throw new S3FileDeleteException("Failed to delete the file");
        }
    }
}
