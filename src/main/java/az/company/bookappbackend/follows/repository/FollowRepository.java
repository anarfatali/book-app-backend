package az.company.bookappbackend.follows.repository;

import az.company.bookappbackend.follows.entities.FollowEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<FollowEntity, Long> {

    @Query("SELECT f FROM FollowEntity f WHERE f.follower.id = :userID")
    Page<FollowEntity> findUserFolloweesByUserID(Long userID, Pageable pageable);

    @Query("SELECT f FROM FollowEntity f WHERE f.followee.id = :userID")
    Page<FollowEntity> findUserFollowersByUserID(Long userID, Pageable pageable);

    @Query("SELECT f FROM FollowEntity f WHERE f.follower.id = :followerId AND f.followee.id = :userId")
    Optional<FollowEntity> findByFollowerIdAndFolloweeId(@NotNull @Min(1) Long followerId, Long userId);

}
