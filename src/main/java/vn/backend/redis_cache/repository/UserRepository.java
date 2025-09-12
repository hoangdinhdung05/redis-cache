package vn.backend.redis_cache.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.backend.redis_cache.entity.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find user by username
     * @param username the username of the user
     * @return Optional of User
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     * @param email the email of the user
     * @return Optional of User
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by username
     * @param username the username of the user
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if user exists by email
     * @param email the email of the user
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find user by username with roles and permissions
     * @param username the username of the user
     * @return Optional of User with roles and permissions
     */
    @Query("""
            SELECT u
            FROM User u
            JOIN FETCH u.roles r
            JOIN FETCH r.permissions p
            WHERE u.username = :username
            """)
    Optional<User> findByUsernameWithRolesAndPermissions(@Param("username") String username);

    /**
     * Find all active users
     * @return Iterable of active users
     */
    @Query("""
        SELECT u
        FROM User u
        WHERE u.isActive = true
        """)
    Iterable<User> findAllIsActiveUsers();
}
