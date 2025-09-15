package vn.backend.redis_cache.service;

import vn.backend.redis_cache.dto.request.LoginRequest;
import vn.backend.redis_cache.dto.request.RefreshTokenRequest;
import vn.backend.redis_cache.dto.response.AuthResponse;
import vn.backend.redis_cache.dto.response.RefreshTokenResponse;

public interface AuthService {
    /**
     * User login
     * @param request info user
     * @return accessToken
     */
    AuthResponse authenticate(LoginRequest request);

    /**
     * Refresh token
     * @param request token old
     * @return new token
     */
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
}
