package vn.backend.redis_cache.exception;

public abstract class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public abstract String getErrorCode();
}
