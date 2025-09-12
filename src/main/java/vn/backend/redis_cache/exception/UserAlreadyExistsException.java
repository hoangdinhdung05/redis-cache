package vn.backend.redis_cache.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends ApiException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.CONFLICT.value();
    }

    @Override
    public String getErrorCode() {
        return getMessage();
    }
}
