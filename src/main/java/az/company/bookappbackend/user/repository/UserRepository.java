package az.company.bookappbackend.user.repository;

import az.company.bookappbackend.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u.avatarUrl FROM UserEntity u WHERE u.id = :userId")
    Optional<String> findAvatarUrlByUserId(Long userId);

    Page<UserEntity> findAll(Pageable pageable);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT u.id FROM UserEntity u WHERE u.username = :username")
    Optional<Long> userIdByUsername(String username);

}
