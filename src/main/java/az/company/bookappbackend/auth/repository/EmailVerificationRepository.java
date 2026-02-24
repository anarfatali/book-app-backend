package az.company.bookappbackend.auth.repository;

import az.company.bookappbackend.auth.model.EmailVerification;
import az.company.bookappbackend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    void deleteByUser(UserEntity user);

    Optional<EmailVerification> findByUserAndVerifiedAtIsNull(UserEntity user);
}
