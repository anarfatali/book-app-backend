package az.company.bookappbackend.minio.config;

import az.company.bookappbackend.minio.exceptions.MinioException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BucketInitializer {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @PostConstruct
    public void initializeBuckets() {
        String[] defaultBuckets = minioConfig.getDefaultBuckets();

        try {
            for (String bucket : defaultBuckets) {
                boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
                if (!found) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());

                    log.info("Bucket '{}' created successfully.", bucket);
                } else {
                    log.info("Bucket '{}' already exists.", bucket);
                }
            }
        } catch (Exception e) {
            log.error("Error initializing MinIO buckets");

            throw new MinioException(e.getMessage());
        }
    }


}
