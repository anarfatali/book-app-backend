package az.company.bookappbackend.storage_service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public interface StorageService {

    String uploadFile(String fileName, String bucketName, MultipartFile file);

    String uploadFile(String filename, String bucketName, byte[] file, String contentType);

    Optional<FileContent> findFile(String fileName, String bucketName);

    void deleteFile(String fileName, String bucketName);
}
