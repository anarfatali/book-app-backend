package az.company.bookappbackend.feed.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "feed_items")
@CompoundIndexes({
        @CompoundIndex(name = "user_created_idx", def = "{'userId': 1, 'createdAt': -1}"),
        @CompoundIndex(name = "uniq_user_source", def = "{'userId': 1, 'sourceType': 1, 'sourceId': 1}", unique = true)
})
public class FeedItemDocument {

    @Id
    private String id;

    private Long userId;
    private String sourceType;
    private Long sourceId;

    private Long authorId;
    private String authorName;
    private String captionExcerpt;
    private String thumbnailUrl;

    @Builder.Default
    private boolean isRead = false;

    @Builder.Default
    private boolean isHidden = false;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
