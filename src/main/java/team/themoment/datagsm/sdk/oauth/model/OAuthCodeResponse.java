package team.themoment.datagsm.sdk.oauth.model;

/**
 * OAuth Code 응답 (레거시)
 * 
 * @deprecated 표준 OAuth 2.0 플로우를 사용하세요
 */
@Deprecated
public class OAuthCodeResponse {
    private String code;

    public OAuthCodeResponse() {}

    public OAuthCodeResponse(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "OAuthCodeResponse{" +
                "code='" + (code != null ? "[REDACTED]" : "null") + '\'' +
                '}';
    }
}
