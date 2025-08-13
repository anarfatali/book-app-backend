package az.company.bookappbackend.feed.entity;

import az.company.bookappbackend.common.enums.FeedType;
import az.company.bookappbackend.social.entity.PostEntity;
import az.company.bookappbackend.social.entity.ReviewEntity;
import az.company.bookappbackend.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "feed_items")
public class FeedItemEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5193848332300472943L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewEntity review;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private FeedType sourceType;

    @Column(name = "is_read")
    @Builder.Default
    private boolean isRead = false;

    @Column(name = "is_hidden")
    @Builder.Default
    private boolean isHidden = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;
}
