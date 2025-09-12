package vn.backend.redis_cache.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.backend.redis_cache.entity.User;
import vn.backend.redis_cache.repository.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user in database with username={}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // convert role(s) to GrantedAuthority
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> {
                        String roleName = role.getRoleName(); // lấy role_name thay vì name
                        if (!roleName.startsWith("ROLE_")) {
                            roleName = "ROLE_" + roleName;
                        }
                    return new SimpleGrantedAuthority(roleName);
                })
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
