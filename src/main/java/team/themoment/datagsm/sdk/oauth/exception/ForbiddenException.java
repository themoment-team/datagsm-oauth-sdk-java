package team.themoment.datagsm.sdk.oauth.exception;

/**
 * 403 Forbidden - 필요한 권한 범위가 없음
 */
public class ForbiddenException extends DataGsmException {
    public ForbiddenException(String message) {
        super(message, 403);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, 403, cause);
    }
}