package team.themoment.datagsm.sdk.oauth.model;

/**
 * 공통 API 응답 래퍼
 *
 * @param <T> 데이터 타입
 */
public class CommonApiResponse<T> {
    private String status;
    private Integer code;
    private String message;
    private T data;

    public CommonApiResponse() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CommonApiResponse{" +
                "status='" + status + '\'' +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
