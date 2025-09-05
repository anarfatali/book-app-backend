package az.company.bookappbackend.follows.entities;

import az.company.bookappbackend.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "blocks",
        indexes = {
                @Index(name = "idx_follow_requests_from_to", columnList = "from_id, to_id", unique = true),
                @Index(name = "idx_follow_requests_to_id", columnList = "to_id"),
                @Index(name = "idx_follow_requests_from_id", columnList = "from_id")
        })
public class FollowRequestEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L + 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_id")
    private UserEntity fromUser;


    @OneToOne
    @JoinColumn(name = "to_id")
    private UserEntity toUser;

    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false)
    private Instant requestedAt;
}
