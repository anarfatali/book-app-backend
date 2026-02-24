package az.company.bookappbackend.follows.entities;

import az.company.bookappbackend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "blocks",
        indexes = {
                @Index(name = "idx_blocks_blocker_id", columnList = "blocker_id"),
                @Index(name = "idx_blocks_blocked_id", columnList = "blocked_id"),
        }
)
public class BlockEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L + 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "blocker_id", nullable = false)
    private UserEntity blocker;

    @ManyToOne
    @JoinColumn(name = "blocked_id", nullable = false)
    private UserEntity blockedUser;

    @Column(name = "blocked_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant blockedAt;
}
