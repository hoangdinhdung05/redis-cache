package vn.backend.redis_cache.exception;

import lombok.Data;
import java.util.Date;

@Data
public class ErrorResponse {
    private Date timestamp;
    private int status;
    private String message;
}
