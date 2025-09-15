package vn.backend.redis_cache.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.backend.redis_cache.config.RedisTTLProperties;
import vn.backend.redis_cache.dto.request.LoginRequest;
import vn.backend.redis_cache.dto.request.RefreshTokenRequest;
import vn.backend.redis_cache.dto.response.AuthResponse;
import vn.backend.redis_cache.dto.response.RefreshTokenResponse;
import vn.backend.redis_cache.repository.UserRepository;
import vn.backend.redis_cache.security.JwtProvider;
import vn.backend.redis_cache.service.AuthService;
import vn.backend.redis_cache.service.RedisService;
import vn.backend.redis_cache.utils.RedisKeyUtil;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final RedisKeyUtil redisKeyUtil;
    private final RedisTTLProperties ttl;

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
        String accessToken = jwtProvider.generateAccessToken(authentication);
        String username = jwtProvider.getUserNameFromJwtToken(accessToken);
        String refreshToken = jwtProvider.generateRefreshToken(username);

        //redis
        long accessTtl = jwtProvider.getExpirationFromToken(accessToken);
        log.info("Set access token with key={} ttl(ms)={} value={}",
                redisKeyUtil.accessTokenKey(username), accessTtl, accessToken);

        redisService.set(redisKeyUtil.accessTokenKey(username),
                accessToken,
                accessTtl,
                TimeUnit.MILLISECONDS);

        long refreshTtl = jwtProvider.getExpirationFromToken(refreshToken);
        log.info("Set refresh token with key={} ttl(ms)={} value={}",
                redisKeyUtil.refreshTokenKey(username), refreshTtl, refreshToken);

        redisService.set(redisKeyUtil.refreshTokenKey(username),
                refreshToken,
                refreshTtl,
                TimeUnit.MILLISECONDS);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
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

        //check redis
        String key = redisKeyUtil.refreshTokenKey(username);
        String storedRefresh = redisService.getString(key);

        log.info("Check refresh token with key={} stored={} request={}",
                key, storedRefresh, request.getRefreshToken());

        if (storedRefresh == null || !storedRefresh.trim().replace("\"", "").equals(request.getRefreshToken())) {
            throw new RuntimeException("Refresh token not found or mismatch");
        }

        var userDetails = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        String accessToken = jwtProvider.generateAccessToken(authentication);

        //redis
        redisService.delete(redisKeyUtil.accessTokenKey(username));
        long accessTtl = jwtProvider.getExpirationFromToken(accessToken);
        redisService.set(
                redisKeyUtil.accessTokenKey(username),
                accessToken,
                accessTtl,
                TimeUnit.MILLISECONDS
        );

        log.info("Refresh token success, create new access token with username: {}", username);
        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}
