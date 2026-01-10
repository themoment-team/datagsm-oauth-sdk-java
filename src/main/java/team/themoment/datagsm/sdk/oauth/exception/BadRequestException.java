package team.themoment.datagsm.sdk.oauth.exception;

/**
 * 400 Bad Request - 잘못된 요청 파라미터
 */
public class BadRequestException extends DataGsmException {
    public BadRequestException(String message) {
        super(message, 400);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, 400, cause);
    }
}