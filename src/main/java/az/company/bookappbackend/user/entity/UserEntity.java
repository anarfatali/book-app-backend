package az.company.bookappbackend.user.entity;

import az.company.bookappbackend.achievement.entity.AchievementEntity;
import az.company.bookappbackend.audit.entity.AuditLogEntity;
import az.company.bookappbackend.common.enums.Interests;
import az.company.bookappbackend.common.enums.ReadingFrequency;
import az.company.bookappbackend.common.enums.SubscriptionType;
import az.company.bookappbackend.exchange.entity.ExchangeEntryEntity;
import az.company.bookappbackend.exchange.entity.ExchangeRequestEntity;
import az.company.bookappbackend.library.entity.LibraryEntryEntity;
import az.company.bookappbackend.library.entity.WishlistItemEntity;
import az.company.bookappbackend.notification.entity.NotificationEntity;
import az.company.bookappbackend.review.entity.ReviewEntity;
import az.company.bookappbackend.social.entity.CommentEntity;
import az.company.bookappbackend.social.entity.CommentLikeEntity;
import az.company.bookappbackend.social.entity.CommentReplyEntity;
import az.company.bookappbackend.social.entity.FollowEntity;
import az.company.bookappbackend.social.entity.LikeEntity;
import az.company.bookappbackend.social.entity.PostEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean verified;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 100, nullable = false, unique = true)
    private String username;

    @Column(length = 100, nullable = false)
    private String surname;

    @Column(name = "subscription_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;

    @Column(name = "reading_frequency", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReadingFrequency readingFrequency;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(length = 500)
    private String bio;

    @Column(name = "reading_experience", length = 100)
    private String readingExperience;

    @Column(length = 200)
    @Enumerated(EnumType.STRING)
    private Interests interests;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    @Column(name = "notification_preference", nullable = false)
    private boolean notificationPreference = true;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostEntity> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LikeEntity> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentLikeEntity> commentLikes;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentReplyEntity> commentReplies;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FollowEntity> following;

    @OneToMany(mappedBy = "followee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FollowEntity> followers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LibraryEntryEntity> libraryEntries;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WishlistItemEntity> wishlistItems;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExchangeEntryEntity> exchangeListings;

    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExchangeRequestEntity> exchangeRequests;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewEntity> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AchievementEntity> achievements;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NotificationEntity> notifications;

    @OneToMany(mappedBy = "actorId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AuditLogEntity> auditLogs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostEntity> likedPosts;
}
