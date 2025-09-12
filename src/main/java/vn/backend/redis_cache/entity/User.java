package vn.backend.redis_cache.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Thêm roles
        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));

            // Thêm permissions
            role.getPermissions().forEach(permission ->
                    authorities.add(new SimpleGrantedAuthority(permission.getPermissionName()))
            );
        });

        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }

    // Helper methods
    public Set<String> getRoleNames() {
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
    }

    public Set<String> getPermissionNames() {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermissionName)
                .collect(Collectors.toSet());
    }
}