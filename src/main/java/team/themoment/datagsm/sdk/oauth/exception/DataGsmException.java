package team.themoment.datagsm.sdk.oauth.exception;

/**
 * DataGSM OAuth SDK 기본 예외 클래스
 * <p>
 * 서버 에러 응답은 두 가지 형식으로 내려오며, SDK는 둘 다 파싱해 {@link #getErrorCode()}와
 * {@link #getErrorDescription()}에 담습니다.
 * <ul>
 *     <li>OAuth 2.0 에러 ({@code /v1/oauth/token} 등): {@code {"error": "invalid_grant", "error_description": "..."}}
 *     → errorCode = {@code invalid_grant}</li>
 *     <li>CommonApiResponse ({@code /userinfo} 인증 실패, rate limit 등): {@code {"status": "UNAUTHORIZED", "code": 401, "message": "..."}}
 *     → errorCode = {@code UNAUTHORIZED}</li>
 * </ul>
 */
public class DataGsmException extends RuntimeException {
    /**
     * HTTP 상태 코드가 없는 경우 (네트워크 에러, 파싱 에러 등)
     */
    public static final int NO_STATUS_CODE = -1;

    private final int statusCode;
    private final String errorCode;
    private final String errorDescription;
    private final String responseBody;

    public DataGsmException(String message) {
        this(message, NO_STATUS_CODE, null, null, null, null);
    }

    public DataGsmException(String message, int statusCode) {
        this(message, statusCode, null, null, null, null);
    }

    public DataGsmException(String message, Throwable cause) {
        this(message, NO_STATUS_CODE, null, null, null, cause);
    }

    public DataGsmException(String message, int statusCode, Throwable cause) {
        this(message, statusCode, null, null, null, cause);
    }

    /**
     * 서버 에러 응답 정보를 포함한 예외 생성
     *
     * @param message          예외 메시지
     * @param statusCode       HTTP 상태 코드
     * @param errorCode        서버 에러 코드 (OAuth {@code error} 또는 CommonApiResponse {@code status}), 없으면 null
     * @param errorDescription 서버 에러 설명 (OAuth {@code error_description} 또는 CommonApiResponse {@code message}), 없으면 null
     * @param responseBody     원본 응답 본문, 없으면 null
     */
    public DataGsmException(String message, int statusCode, String errorCode, String errorDescription, String responseBody) {
        this(message, statusCode, errorCode, errorDescription, responseBody, null);
    }

    private DataGsmException(String message, int statusCode, String errorCode, String errorDescription,
                             String responseBody, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.responseBody = responseBody;
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

    /**
     * 서버 에러 코드. OAuth 에러면 {@code invalid_grant} 같은 {@code error} 값,
     * CommonApiResponse면 {@code UNAUTHORIZED} 같은 {@code status} 값입니다. 파싱할 수 없으면 null입니다.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 서버 에러 설명. OAuth {@code error_description} 또는 CommonApiResponse {@code message}입니다. 없으면 null입니다.
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * 원본 응답 본문. HTTP 응답이 없었거나 본문이 비어 있으면 null입니다.
     */
    public String getResponseBody() {
        return responseBody;
    }
}
