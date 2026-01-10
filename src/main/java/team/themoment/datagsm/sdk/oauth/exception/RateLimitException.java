package team.themoment.datagsm.sdk.oauth.exception;

/**
 * 429 Too Many Requests - 단위 시간에 너무 많은 요청량 발생
 */
public class RateLimitException extends DataGsmException {
    public RateLimitException(String message) {
        super(message, 429);
    }

    public RateLimitException(String message, Throwable cause) {
        super(message, 429, cause);
    }
}