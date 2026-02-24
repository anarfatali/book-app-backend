package az.company.bookappbackend.book.entity;

import az.company.bookappbackend.exchange.entity.ExchangeOfferEntity;
import az.company.bookappbackend.social.entity.ReviewEntity;
import az.company.bookappbackend.wishlist.WishlistItemEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "books",
        indexes = {
                @Index(name = "idx_books_author", columnList = "author"),
                @Index(name = "idx_books_category", columnList = "category"),
                @Index(name = "idx_books_publish_date", columnList = "publish_date")
        }
)
@EqualsAndHashCode(exclude = {"exchangeOffers", "wishlistItems", "reviews"})
public class BookEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1039133941493493223L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private Integer pageCount;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private LocalDate publishDate;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String category;

    @Column
    @Builder.Default
    private Integer ratings = 0;

    @Column
    @Builder.Default
    private Double avgScore = 0.0;

    @Column
    private String coverImageUrl;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ExchangeOfferEntity> exchangeOffers = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<WishlistItemEntity> wishlistItems = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ReviewEntity> reviews = new HashSet<>();
}
