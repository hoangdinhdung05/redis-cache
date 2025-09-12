package vn.backend.redis_cache.exception;

import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends ApiException {

    public RoleNotFoundException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }

    @Override
    public String getErrorCode() {
        return getMessage();
    }
}
