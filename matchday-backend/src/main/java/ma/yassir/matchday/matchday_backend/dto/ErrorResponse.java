package ma.yassir.matchday.matchday_backend.dto;

import org.slf4j.MDC;

public record ErrorResponse(String error,
                            String message,
                            Long retryAfterSeconds,
                            String requestId ) {}