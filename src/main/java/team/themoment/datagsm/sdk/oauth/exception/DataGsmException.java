package team.themoment.datagsm.sdk.oauth.exception;

/**
 * DataGSM OpenAPI SDK 기본 예외 클래스
 */
public class DataGsmException extends RuntimeException {
    /**
     * HTTP 상태 코드가 없는 경우 (네트워크 에러, 파싱 에러 등)
     */
    public static final int NO_STATUS_CODE = -1;

    private final int statusCode;

    public DataGsmException(String message) {
        super(message);
        this.statusCode = NO_STATUS_CODE;
    }

    public DataGsmException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public DataGsmException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = NO_STATUS_CODE;
    }

    public DataGsmException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * HTTP 상태 코드 반환.
     * HTTP 응답이 없는 에러(네트워크 에러 등)의 경우 {@link #NO_STATUS_CODE}(-1)를 반환합니다.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * HTTP 상태 코드가 있는지 여부 반환
     */
    public boolean hasStatusCode() {
        return statusCode != NO_STATUS_CODE;
    }
}