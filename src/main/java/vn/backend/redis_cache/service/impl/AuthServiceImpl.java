package vn.backend.redis_cache.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.backend.redis_cache.dto.request.LoginRequest;
import vn.backend.redis_cache.dto.request.RefreshTokenRequest;
import vn.backend.redis_cache.dto.response.AuthResponse;
import vn.backend.redis_cache.dto.response.RefreshTokenResponse;
import vn.backend.redis_cache.repository.UserRepository;
import vn.backend.redis_cache.security.JwtProvider;
import vn.backend.redis_cache.service.AuthService;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * User login
     * @param request info user
     * @return accessToken
     */
    @Override
    public AuthResponse authenticate(LoginRequest request) {
        log.info("Authenticate user (LOGIN) with username:{}", request.getUsername());
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //build token
        String token = jwtProvider.generateAccessToken(authentication);
        String username = jwtProvider.getUserNameFromJwtToken(token);
        String refresh = jwtProvider.generateRefreshToken(username);
        return AuthResponse.builder()
                .accessToken(token)
                .refreshToken(refresh)
                .build();
    }

    /**
     * Refresh token
     * @param request token old
     * @return new token
     */
    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        if (!jwtProvider.validateToken(request.getRefreshToken())) {
            throw new RuntimeException("Invalid or expired refresh token");
        }
        String username = jwtProvider.getUserNameFromJwtToken(request.getRefreshToken());
        var userDetails = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        String accessToken = jwtProvider.generateAccessToken(authentication);
        log.info("Refresh token create new access token with username:{}", username);
        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
