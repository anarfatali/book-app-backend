package az.company.bookappbackend.wishlist;

import az.company.bookappbackend.book.entity.BookEntity;
import az.company.bookappbackend.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@Table(
        name = "wishlist_items",
        indexes = {
                @Index(name = "idx_wishlist_user", columnList = "user_id"),
                @Index(name = "idx_wishlist_list", columnList = "wishlist_id"),
                @Index(name = "idx_wishlist_book", columnList = "book_id")
        }
)
@EqualsAndHashCode(exclude = {"user", "book"})
public class WishlistItemEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 7813131391221839382L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private WishlistEntity wishlist;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Column(name = "added_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant addedAt;
}
