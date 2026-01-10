package team.themoment.datagsm.sdk.oauth.exception;

/**
 * 404 Not Found 예외
 */
public class NotFoundException extends DataGsmException {
    public NotFoundException(String message) {
        super(message, 404);
    }
}
