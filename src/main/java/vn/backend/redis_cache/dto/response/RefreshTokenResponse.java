package vn.backend.redis_cache.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenResponse {
    private String accessToken;
}
