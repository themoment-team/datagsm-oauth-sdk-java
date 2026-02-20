package team.themoment.datagsm.sdk.oauth.model;

import com.google.gson.annotations.SerializedName;

/**
 * OAuth 2.0 토큰 응답 (RFC 6749)
 * 
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-5.1">RFC 6749 Section 5.1</a>
 */
public class TokenResponse {
    /**
     * Access Token (필수)
     */
    @SerializedName("access_token")
    private String accessToken;
    
    /**
     * Token 타입 (필수, 보통 "Bearer")
     */
    @SerializedName("token_type")
    private String tokenType;
    
    /**
     * Access Token 만료 시간 (초 단위, 선택)
     */
    @SerializedName("expires_in")
    private Long expiresIn;
    
    /**
     * Refresh Token (선택, client_credentials grant에서는 없음)
     */
    @SerializedName("refresh_token")
    private String refreshToken;
    
    /**
     * 부여된 권한 범위 (선택)
     */
    @SerializedName("scope")
    private String scope;

    public TokenResponse() {}

    public TokenResponse(String accessToken, String tokenType, Long expiresIn, String refreshToken, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                "accessToken='" + (accessToken != null ? "[REDACTED]" : "null") + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", refreshToken='" + (refreshToken != null ? "[REDACTED]" : "null") + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
