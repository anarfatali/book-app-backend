package az.company.bookappbackend.follows.repository;

import az.company.bookappbackend.follows.entities.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<BlockEntity, Long> {

    @Query("SELECT b FROM BlockEntity b WHERE b.blocker.id = :blockerID AND b.blockedUser.id = :blockedID")
    Optional<Object> findByBlockerAndBlockedUser(Long blockerID, Long blockedID);
}
