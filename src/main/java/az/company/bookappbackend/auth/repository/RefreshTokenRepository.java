package az.company.bookappbackend.auth.repository;

import az.company.bookappbackend.auth.entity.RefreshToken;
import az.company.bookappbackend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteAllByUser(UserEntity user);
}
