package team.themoment.datagsm.sdk.oauth.model;

import com.google.gson.annotations.SerializedName;
import team.themoment.datagsm.sdk.oauth.util.ScopeUtils;

import java.util.List;

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

    /**
     * OIDC ID Token (선택, authorization_code grant에서 {@code openid} scope를 요청한 경우에만 발급)
     * <p>
     * SDK는 서명을 검증하지 않습니다. 사용자 식별에 쓰려면 {@code jwks_uri}의 공개키로
     * 서명(RS256)과 iss, aud, exp, nonce를 직접 검증하세요.
     */
    @SerializedName("id_token")
    private String idToken;

    public TokenResponse() {}

    public TokenResponse(String accessToken, String tokenType, Long expiresIn, String refreshToken, String scope) {
        this(accessToken, tokenType, expiresIn, refreshToken, scope, null);
    }

    public TokenResponse(String accessToken, String tokenType, Long expiresIn, String refreshToken, String scope, String idToken) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.scope = scope;
        this.idToken = idToken;
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

    /**
     * 부여된 scope를 목록으로 반환합니다. (공백 구분 문자열을 분리)
     *
     * @return 불변 scope 목록 (scope가 없으면 빈 목록)
     */
    public List<String> getScopes() {
        return ScopeUtils.split(scope);
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                "accessToken='" + (accessToken != null ? "[REDACTED]" : "null") + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", refreshToken='" + (refreshToken != null ? "[REDACTED]" : "null") + '\'' +
                ", scope='" + scope + '\'' +
                ", idToken='" + (idToken != null ? "[REDACTED]" : "null") + '\'' +
                '}';
    }
}
