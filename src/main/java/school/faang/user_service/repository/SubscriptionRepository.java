package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface SubscriptionRepository extends CrudRepository<User, Long> {

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
            where subs.followee_id = :userId
            """)
    // old method name Stream<User> findByFolloweeId(long followeeId);
    Stream<User> findFolloweesByUserId(long userId);

    @Query(nativeQuery = true, value = "select count(id) from subscription where followee_id = :userId")
    // old method name int findFollowersAmountByFolloweeId(long followeeId);
    int findFollowersAmountByUserId(long userId);

    @Query(nativeQuery = true, value = """
            select u.* from users as u
            join subscription as subs on u.id = subs.followee_id
            where subs.follower_id = :userId
            """)
    // old method name Stream<User> findByFollowerId(long followerId);
    Stream<User> findFollowersByUserId(long userId);

    @Query(nativeQuery = true, value = "select count(id) from subscription where follower_id = :userId")
    // old method name int findFolloweesAmountByFollowerId(long followerId);
    int findFolloweesAmountByUserId(long userId);
}