package team.themoment.datagsm.sdk.oauth.exception;

/**
 * 500 Internal Server Error - 서버 내부 오류 발생
 */
public class ServerErrorException extends DataGsmException {
    public ServerErrorException(String message) {
        super(message, 500);
    }

    public ServerErrorException(String message, Throwable cause) {
        super(message, 500, cause);
    }
}