package vn.backend.redis_cache.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.util.Date;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, WebRequest request) {
        ErrorResponse response = buildErrorResponse(
                ex, request,
                HttpStatus.valueOf(ex.getStatusCode()),
                ex.getErrorCode(),
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class
    })
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(Exception e, WebRequest request) {
        String message;
        String error;

        if (e instanceof MethodArgumentNotValidException manve) {
            message = manve.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)   // chỉ lấy message
                    .collect(Collectors.joining("; "));
            error = "INVALID_PAYLOAD";
        } else if (e instanceof MissingServletRequestParameterException msrp) {
            message = msrp.getParameterName() + " parameter is missing";
            error = "MISSING_PARAMETER";
        } else if (e instanceof ConstraintViolationException cve) {
            message = cve.getMessage();
            error = "INVALID_PARAMETER";
        } else {
            message = e.getMessage();
            error = "VALIDATION_ERROR";
        }

        return buildErrorResponse(e, request, BAD_REQUEST, error, message);
    }

    //Auth error
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse handleBadCredentials(BadCredentialsException e, WebRequest request) {
        return buildErrorResponse(e, request, UNAUTHORIZED, "BAD_CREDENTIALS", e.getMessage());
    }

    //Fallback
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllUncaughtException(Exception e, WebRequest request) {
        log.error("Unhandled exception occurred", e);
        return buildErrorResponse(e, request, INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", e.getMessage());
    }


    private ErrorResponse buildErrorResponse(Exception e, WebRequest request,
                                             HttpStatus status,
                                             String error,
                                             String message) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(new Date());
        response.setStatus(status.value());
        response.setMessage(message);
        return response;
    }
}
