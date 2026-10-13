package team.themoment.datagsm.sdk.oauth.exception;

/**
 * 5xx Server Error - 서버 내부 오류 발생 (500, 502, 503, 504)
 */
public class ServerErrorException extends DataGsmException {
    public ServerErrorException(String message) {
        super(message, 500);
    }

    public ServerErrorException(String message, Throwable cause) {
        super(message, 500, cause);
    }

    /**
     * @param statusCode 실제 HTTP 상태 코드 (500, 502, 503, 504 등)
     */
    public ServerErrorException(String message, int statusCode, String errorCode, String errorDescription, String responseBody) {
        super(message, statusCode, errorCode, errorDescription, responseBody);
    }
}
