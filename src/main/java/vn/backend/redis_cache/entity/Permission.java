package vn.backend.redis_cache.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity {

    @Column(name = "permission_name", unique = true, nullable = false, length = 100)
    private String permissionName;

    @Column(name = "resource", nullable = false, length = 50)
    private String resource;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "permissions")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + getId() +
                ", name='" + permissionName + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
