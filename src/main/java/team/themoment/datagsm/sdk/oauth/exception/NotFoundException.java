package team.themoment.datagsm.sdk.oauth.exception;

/**
 * 404 Not Found 예외
 */
public class NotFoundException extends DataGsmException {
    public NotFoundException(String message) {
        super(message, 404);
    }

    public NotFoundException(String message, String errorCode, String errorDescription, String responseBody) {
        super(message, 404, errorCode, errorDescription, responseBody);
    }
}
