package az.company.bookappbackend.storage_service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StorageConstants {

    public static final String PROFILE_PICTURE_BUCKET = "profile_picture_bucket";
    // in future, there will be buckets for posts
}
