package az.company.bookappbackend.exchange.entity;

import az.company.bookappbackend.book.entity.BookEntity;
import az.company.bookappbackend.common.enums.ExchangeRequestStatus;
import az.company.bookappbackend.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "exchange_requests",
        indexes = {
                @Index(name = "idx_requests_offer_id", columnList = "offer_id"),
                @Index(name = "idx_requests_requester_id", columnList = "requester_id"),
                @Index(name = "idx_requests_created_at", columnList = "created_at")
        }
)
@EqualsAndHashCode(exclude = {"offer", "requester", "offeredBook"})
public class ExchangeRequestEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 2902507982472302434L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private ExchangeOfferEntity offer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private UserEntity requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offered_book_id")
    private BookEntity offeredBook;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ExchangeRequestStatus status = ExchangeRequestStatus.PENDING;

    @Column(length = 1000)
    private String message;

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;
}
