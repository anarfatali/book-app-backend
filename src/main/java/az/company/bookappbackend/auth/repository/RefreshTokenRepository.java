package az.company.bookappbackend.auth.repository;

import az.company.bookappbackend.auth.entity.RefreshToken;
import az.company.bookappbackend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteAllByUser(UserEntity user);

    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String encode);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user AND rt.revoked = false")
    void revokeAllByUser(UserEntity user);
}
