package ma.yassir.matchday.matchday_backend.controller;



import ma.yassir.matchday.matchday_backend.service.CooldownActiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ma.yassir.matchday.matchday_backend.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CooldownActiveException.class)
    public ResponseEntity<ErrorResponse> handCoolDown(CooldownActiveException ex) {

        log.debug("returning 429 cooldown (retryAfterSeconds {})", ex.getRetryAfterSeconds());
        HttpHeaders headers = new HttpHeaders();

        headers.add("Retry-After", String.valueOf(ex.getRetryAfterSeconds()));
        return ResponseEntity.status(429)
                .headers(headers).body(new ErrorResponse(
                        "COOLDOWN_ACTIVE",
                        "You can update this venue again later.",
                        ex.getRetryAfterSeconds(),
                        MDC.get("requestId")));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                "VALIDATION_ERROR",
                "Invalid request body",
                null,
                MDC.get("requestId")));
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MissingRequestHeaderException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleMalformedRequest(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                "BAD_REQUEST",
                "Missing or invalid request input.",
                null,
                MDC.get("requestId")
        ));
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                "BAD_REQUEST",
                ex.getMessage(),
                null,
                MDC.get("requestId")
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex) {
        log.error("unknown exception", ex);
        return ResponseEntity.status(500).body(new ErrorResponse("INTERNAL_ERROR",
                "Unexpected error.",
                null,
                MDC.get("requestId")));
    }
}
