package team.themoment.datagsm.sdk.oauth.model;

import com.google.gson.annotations.SerializedName;

/**
 * OAuth Code 요청 (레거시 엔드포인트 POST /v1/oauth/code)
 * 
 * @deprecated 표준 OAuth 2.0 플로우를 사용하세요 (GET /v1/oauth/authorize)
 */
@Deprecated
public class OAuthCodeRequest {
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("clientId")
    private String clientId;
    
    @SerializedName("redirectUrl")
    private String redirectUrl;
    
    @SerializedName("codeChallenge")
    private String codeChallenge;
    
    @SerializedName("codeChallengeMethod")
    private String codeChallengeMethod;

    public OAuthCodeRequest() {}

    public OAuthCodeRequest(String email, String password, String clientId, String redirectUrl) {
        this.email = email;
        this.password = password;
        this.clientId = clientId;
        this.redirectUrl = redirectUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getCodeChallenge() {
        return codeChallenge;
    }

    public void setCodeChallenge(String codeChallenge) {
        this.codeChallenge = codeChallenge;
    }

    public String getCodeChallengeMethod() {
        return codeChallengeMethod;
    }

    public void setCodeChallengeMethod(String codeChallengeMethod) {
        this.codeChallengeMethod = codeChallengeMethod;
    }

    @Override
    public String toString() {
        return "OAuthCodeRequest{" +
                "email='" + email + '\'' +
                ", password='[REDACTED]'" +
                ", clientId='" + clientId + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", codeChallenge='" + codeChallenge + '\'' +
                ", codeChallengeMethod='" + codeChallengeMethod + '\'' +
                '}';
    }
}
