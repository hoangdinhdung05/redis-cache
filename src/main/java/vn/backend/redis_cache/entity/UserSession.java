package vn.backend.redis_cache.entity;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession implements Serializable {

    private String username;
    private String fullName;
    private Set<String> roles;
    private Set<String> permissions;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivity;
    private String ipAddress;
    private String userAgent;
    private Boolean isActive;

    @Override
    public String toString() {
        return "UserSession{" +
                "username='" + username + '\'' +
                ", loginTime=" + loginTime +
                ", lastActivity=" + lastActivity +
                ", isActive=" + isActive +
                '}';
    }
}