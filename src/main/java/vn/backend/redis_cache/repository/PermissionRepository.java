package vn.backend.redis_cache.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.backend.redis_cache.entity.Permission;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Find permission by name
     * @param name the name of the permission
     * @return Optional of Permission
     */
    Optional<Permission> findByName(String name);

    /**
     * Check if permission exists by name
     * @param name the name of the permission
     * @return true if permission exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Find permissions by resource
     * @param resource the resource of the permission
     * @return Set of Permissions
     */
    Set<Permission> findByResource(String resource);

    /**
     * Find permission by resource and action
     * @param resource the resource of the permission
     * @param action the action of the permission
     * @return Optional of Permission
     */
    @Query("""
            SELECT p
            FROM Permission p
            WHERE p.resource = :resource
            AND p.action = :action
            """)
    Optional<Permission> findByResourceAndAction(@Param("resource") String resource,
                                                 @Param("action") String action);

}
