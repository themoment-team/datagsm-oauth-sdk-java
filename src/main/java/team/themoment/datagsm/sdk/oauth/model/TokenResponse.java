package team.themoment.datagsm.sdk.oauth.model;

/**
 * OAuth 토큰 응답
 */
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    public TokenResponse() {}

    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                "accessToken='" + (accessToken != null ? "[REDACTED]" : "null") + '\'' +
                ", refreshToken='" + (refreshToken != null ? "[REDACTED]" : "null") + '\'' +
                '}';
    }
}
