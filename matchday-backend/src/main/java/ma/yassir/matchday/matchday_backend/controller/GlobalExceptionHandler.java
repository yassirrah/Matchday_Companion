package ma.yassir.matchday.matchday_backend.controller;



import ma.yassir.matchday.matchday_backend.service.CooldownActiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ma.yassir.matchday.matchday_backend.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(CooldownActiveException.class)
    public ResponseEntity<ErrorResponse> handCoolDown(CooldownActiveException ex){

        log.debug("returning 429 cooldown (retryAfterSeconds {})", ex.getRetryAfterSeconds());
        HttpHeaders headers = new HttpHeaders();

        headers.add("Retry-after", String.valueOf(ex.getRetryAfterSeconds()));
        return ResponseEntity.status(429)
                .headers(headers).body(new ErrorResponse("COOLDOWN_ACTIVE",
                        "You can update this venue again later.",
                        ex.getRetryAfterSeconds()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(
                "VALIDATION_ERROR",
                "Invalid request body",
                null
        ));
    }

}
