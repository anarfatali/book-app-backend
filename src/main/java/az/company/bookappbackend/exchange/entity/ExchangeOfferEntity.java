package az.company.bookappbackend.exchange.entity;

import az.company.bookappbackend.book.entity.BookEntity;
import az.company.bookappbackend.common.enums.BookCondition;
import az.company.bookappbackend.common.enums.OfferAvailability;
import az.company.bookappbackend.user.entity.UserEntity;
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
import jakarta.persistence.ManyToOne;
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
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exchange_offers")
@EqualsAndHashCode(exclude = {"owner", "book", "photoUrls"})
public class ExchangeOfferEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 7849824380229424720L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity owner;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookCondition condition;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OfferAvailability availability = OfferAvailability.AVAILABLE;

    @ElementCollection
    @Column(name = "url", length = 1000)
    @CollectionTable(name = "offer_photo_urls", joinColumns = @JoinColumn(name = "offer_id"))
    private List<String> photoUrls = new ArrayList<>();

    @Column(name = "is_approved")
    @Builder.Default
    private boolean isApproved = false;

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;
}
