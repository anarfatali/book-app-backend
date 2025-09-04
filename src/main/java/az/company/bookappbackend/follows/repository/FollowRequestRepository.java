package az.company.bookappbackend.follows.repository;

import az.company.bookappbackend.follows.entities.FollowRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRequestRepository extends PagingAndSortingRepository<FollowRequestEntity, Long> {

    // TODO: fix n+1 problem
    @Query("SELECT fr FROM FollowRequestEntity fr WHERE fr.fromUser.id = :userID")
    Page<FollowRequestEntity> findAllOutgoingFollowRequestsOfUser(Long userID, Pageable pageable);

    @Query("SELECT fr FROM FollowRequestEntity fr WHERE fr.toUser.id = :userID")
    Page<FollowRequestEntity> findAllIncomingFollowRequestsOfUser(Long userID, Pageable pageable);

    @Query("SELECT fr FROM FollowRequestEntity fr WHERE fr.fromUser.id = :id AND fr.toUser.id = :userID")
    Optional<FollowRequestEntity> findByFromUserIdAndToUserId(Long id, Long userID);

    Optional<FollowRequestEntity> findById(Long reqID);

    void save(FollowRequestEntity followRequestEntity);

    void deleteById(Long id);

    void delete(FollowRequestEntity followRequestEntity);
}
