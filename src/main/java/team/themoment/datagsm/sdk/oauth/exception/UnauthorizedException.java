package team.themoment.datagsm.sdk.oauth.exception;

/**
 * 401 Unauthorized - API 키가 유효하지 않거나 만료됨
 */
public class UnauthorizedException extends DataGsmException {
    public UnauthorizedException(String message) {
        super(message, 401);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, 401, cause);
    }
}