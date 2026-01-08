package ma.yassir.matchday.matchday_backend.service;

public class CooldownActiveException extends RuntimeException{


    private final long retryAfterSeconds;

    public CooldownActiveException(long retryAfterSeconds) {
        super("Cool down active");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds(){
        return retryAfterSeconds;
    }
}
