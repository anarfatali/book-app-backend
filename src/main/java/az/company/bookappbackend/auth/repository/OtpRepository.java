package az.company.bookappbackend.auth.repository;

import az.company.bookappbackend.auth.entity.EmailVerification;
import az.company.bookappbackend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<EmailVerification, UUID> {

    Optional<EmailVerification> findByUser(UserEntity user);

    void deleteByUser(UserEntity user);
}
