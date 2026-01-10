package team.themoment.datagsm.sdk.oauth.exception;

/**
 * DataGSM OpenAPI SDK 기본 예외 클래스
 */
public class DataGsmException extends RuntimeException {
    private final int statusCode;

    public DataGsmException(String message) {
        super(message);
        this.statusCode = 0;
    }

    public DataGsmException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public DataGsmException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
    }

    public DataGsmException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}