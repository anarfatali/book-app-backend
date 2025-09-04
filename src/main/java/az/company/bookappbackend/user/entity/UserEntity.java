package az.company.bookappbackend.user.entity;

import az.company.bookappbackend.achievement.entity.AchievementEntity;
import az.company.bookappbackend.audit.entity.AuditLogEntity;
import az.company.bookappbackend.common.enums.Interest;
import az.company.bookappbackend.common.enums.ReadingFrequency;
import az.company.bookappbackend.common.enums.Role;
import az.company.bookappbackend.common.enums.SubscriptionType;
import az.company.bookappbackend.exchange.entity.ExchangeOfferEntity;
import az.company.bookappbackend.exchange.entity.ExchangeRequestEntity;
import az.company.bookappbackend.library.entity.LibraryEntity;
import az.company.bookappbackend.notification.entity.NotificationEntity;
import az.company.bookappbackend.social.entity.CommentEntity;
import az.company.bookappbackend.social.entity.FollowEntity;
import az.company.bookappbackend.social.entity.PostEntity;
import az.company.bookappbackend.social.entity.ReviewEntity;
import az.company.bookappbackend.wishlist.WishlistEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EqualsAndHashCode(exclude = {"posts", "likedPosts", "savedPosts",
        "myComments", "following", "followers", "libraries",
        "wishlist", "exchangeOffers", "exchangeRequests",
        "reviews", "achievements", "notifications", "auditLogs"})
public class UserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 2405172041950251807L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(length = 100, nullable = false, unique = true)
    private String username;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private boolean isVerified = false;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "subscription_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SubscriptionType subscriptionType = SubscriptionType.FREE;

    @Column(name = "reading_frequency", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReadingFrequency readingFrequency;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "bio", length = 250)
    private String bio;

    @Column(name = "reading_experience", length = 100)
    private String readingExperience;

    @ElementCollection(targetClass = Interest.class)
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Interest> interests = new HashSet<>();

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private boolean isPublic = true;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;

    // Helper method
    public void replaceInterests(Set<Interest> newInterests) {
        this.interests.clear();
        this.interests.addAll(newInterests);
    }

// ------------------------- Till here -------------------------

    @Column(name = "notification_preference", nullable = false)
    @Builder.Default
    private boolean notificationPreference = true;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<PostEntity> posts = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "liked_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    @Builder.Default
    private Set<PostEntity> likedPosts = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "saved_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    @Builder.Default
    private Set<PostEntity> savedPosts = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "archived_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    @Builder.Default
    private Set<PostEntity> archivedPosts = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "liked_reviews",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "review_id")
    )
    @Builder.Default
    private Set<ReviewEntity> likedReviews = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "saved_reviews",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "review_id")
    )
    @Builder.Default
    private Set<ReviewEntity> savedReviews = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<CommentEntity> myComments = new ArrayList<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<FollowEntity> following = new HashSet<>();

    @OneToMany(mappedBy = "followee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<FollowEntity> followers = new HashSet<>();

    // library one to one
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<LibraryEntity> libraries = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private WishlistEntity wishlist;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ExchangeOfferEntity> exchangeOffers = new HashSet<>();

    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ExchangeRequestEntity> exchangeRequests = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ReviewEntity> reviews = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<AchievementEntity> achievements = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<NotificationEntity> notifications = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<AuditLogEntity> auditLogs = new HashSet<>();
}
