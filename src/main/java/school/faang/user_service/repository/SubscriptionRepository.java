package school.faang.user_service.repository;

import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.subscription.Subscription;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

    @Query(nativeQuery = true, value = "insert into subscription (follower_id, followee_id) values (:followerId, :followeeId)")
    @Modifying
    void followUser(long followerId, long followeeId);

    @Query(nativeQuery = true, value = "delete from subscription where follower_id = :followerId and followee_id = :followeeId")
    @Modifying
    void unfollowUser(long followerId, long followeeId);

    @Query(nativeQuery = true, value = "select exists(select 1 from subscription where follower_id = :followerId and followee_id = :followeeId)")
    boolean existsByFollowerIdAndFolloweeId(long followerId, long followeeId);

    @Query(nativeQuery = true, value = """
            select u.* from users as u
            join subscription as subs on u.id = subs.follower_id
            where subs.followee_id = :followeeId
            """)
    Stream<User> findByFolloweeId(long followeeId);

    @Query(nativeQuery = true, value = "select count(id) from subscription where followee_id = :followeeId")
    int findFollowersAmountByFolloweeId(long followeeId);

    @Query(nativeQuery = true, value = """
            select u.* from users as u
            join subscription as subs on u.id = subs.followee_id
            where subs.follower_id = :followerId
            """)
    Stream<User> findByFollowerId(long followerId);

    @Query(nativeQuery = true, value = "select count(id) from subscription where follower_id = :followerId")
    int findFolloweesAmountByFollowerId(long followerId);

    @Query(value = """
    SELECT s.follower_id AS userId, array_agg(s.followee_id) AS followeeIds
    FROM subscription s
    GROUP BY s.follower_id
    """, nativeQuery = true)
    Optional<List<Tuple>> findUsersFolloweesTuple();

    @Query(nativeQuery = true, value = """
    SELECT subs.follower_id FROM subscription AS subs
    WHERE subs.followee_id = :followeeId
""")
    List<Long> findFollowerIdsByFolloweeId(long followeeId);
}