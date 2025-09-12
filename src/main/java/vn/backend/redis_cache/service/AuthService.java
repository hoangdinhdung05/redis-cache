package vn.backend.redis_cache.service;

import vn.backend.redis_cache.dto.request.LoginRequest;
import vn.backend.redis_cache.dto.response.AuthResponse;

public interface AuthService {
    /**
     * User login
     * @param request info user
     * @return accessToken
     */
    AuthResponse authenticate(LoginRequest request);
}
