package az.company.bookappbackend.minio.config;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class MinioConfig {
    @Value("${minio.url}")
    private String url;

    @Value("${minio.access.name}")
    private String accessKey;

    @Value("${minio.access.secret}")
    private String accessSecret;

    @Value("${minio.default-buckets}")
    private String[] defaultBuckets;

    @Bean
    public MinioClient minioClient() {
        return MinioClient
                .builder()
                .endpoint(url)
                .credentials(
                        accessKey,
                        accessSecret
                )
                .build();
    }
}
