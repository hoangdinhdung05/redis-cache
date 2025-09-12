package vn.backend.redis_cache.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.backend.redis_cache.entity.Role;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name
     * @param name the name of the role
     * @return Optional of Role
     */
    Optional<Role> findByName(String name);

    /**
     * Check if role exists by name
     * @param name the name of the role
     * @return true if role exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Find role by name with permissions
     * @param name the name of the role
     * @return Optional of Role with permissions
     */
    @Query("""
            SELECT r
            FROM Role r
            JOIN FETCH r.permissions p
            WHERE r.name = :name
            """)
    Optional<Role> findByNameWithPermissions(@Param("name") String name);

    /**
     * Find roles by names with permissions
     * @param names the names of the roles
     * @return Set of Roles with permissions
     */
    @Query("""
            SELECT DISTINCT r
            FROM Role r
            JOIN FETCH r.permissions p
            WHERE r.name IN :names
            """)
    Set<Role> findByNamesWithPermissions(@Param("names") Set<String> names);
}
