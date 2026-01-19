package ma.yassir.matchday.matchday_backend.dto;

public record ErrorResponse(String error,
                            String message,
                            Long retryAfterSeconds,
                            String requestId ) {}