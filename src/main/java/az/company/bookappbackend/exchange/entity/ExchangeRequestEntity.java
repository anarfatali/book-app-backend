package az.company.bookappbackend.exchange.entity;

import az.company.bookappbackend.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exchange_requests")
public class ExchangeRequestEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 9208428482849924424L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private ExchangeEntryEntity listing;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private UserEntity requester;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status; // PENDING, APPROVED, COMPLETED, CANCELLED, REJECTED

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public enum RequestStatus {PENDING, APPROVED, COMPLETED, CANCELLED, REJECTED}

    //requestor requested

    //status of request

    //createdAt

    //requestor library

    //requirer
}
