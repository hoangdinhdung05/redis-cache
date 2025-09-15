package vn.backend.redis_cache.dto.request;

import lombok.Getter;

@Getter
public class RefreshTokenRequest {
    private String refreshToken;
}
