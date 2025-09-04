package az.company.bookappbackend.follows.entities;

import az.company.bookappbackend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowRequestEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L + 1;

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_id")
    private UserEntity fromUser;


    @OneToOne
    @JoinColumn(name = "to_id")
    private UserEntity toUser;

    private Instant requestedAt;
}
