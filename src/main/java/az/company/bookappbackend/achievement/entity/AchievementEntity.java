package az.company.bookappbackend.achievement.entity;

import az.company.bookappbackend.user.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        name = "achievements",
        indexes = {
                @Index(name = "achievement_user_id_idx", columnList = "user_id"),
                @Index(name = "achievement_type_idx", columnList = "type"),
                @Index(name = "achievement_earned_at_idx", columnList = "earned_at")
        }
)
@EqualsAndHashCode(exclude = {"user"})
public class AchievementEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1905122041950251207L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 100)
    private String type;

    @Column
    private Integer points;

    @Column(name = "earned_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant earnedAt;

    @Column(name = "qr_code_url", length = 500)
    private String qrCodeUrl;
}
