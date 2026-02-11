package team.themoment.datagsm.sdk.oauth;

import team.themoment.datagsm.sdk.oauth.client.AccountApi;
import team.themoment.datagsm.sdk.oauth.client.AccountApiImpl;
import team.themoment.datagsm.sdk.oauth.client.OAuthApi;
import team.themoment.datagsm.sdk.oauth.client.OAuthApiImpl;
import team.themoment.datagsm.sdk.oauth.http.HttpClient;
import team.themoment.datagsm.sdk.oauth.http.OkHttpClientImpl;
import team.themoment.datagsm.sdk.oauth.model.TokenResponse;
import team.themoment.datagsm.sdk.oauth.model.UserInfo;

/**
 * DataGSM OAuth SDK 메인 클라이언트
 */
public class DataGsmClient implements AutoCloseable {
    private static final String DEFAULT_AUTHORIZATION_BASE_URL = "https://oauth.data.hellogsm.kr";
    private static final String DEFAULT_USERINFO_BASE_URL = "https://oauth-userinfo.data.hellogsm.kr";

    private final HttpClient httpClient;
    private final OAuthApi oAuthApi;
    private final AccountApi accountApi;

    private DataGsmClient(Builder builder) {
        this.httpClient = builder.httpClient != null ? builder.httpClient : new OkHttpClientImpl();

        String authorizationBaseUrl = builder.authorizationBaseUrl != null ? builder.authorizationBaseUrl : DEFAULT_AUTHORIZATION_BASE_URL;
        String userInfoBaseUrl = builder.userInfoBaseUrl != null ? builder.userInfoBaseUrl : DEFAULT_USERINFO_BASE_URL;

        this.oAuthApi = new OAuthApiImpl(httpClient, builder.clientId, builder.clientSecret, authorizationBaseUrl);
        this.accountApi = new AccountApiImpl(httpClient, userInfoBaseUrl);
    }

    /**
     * Authorization Code를 Access 토큰과 Refresh 토큰으로 교환
     *
     * @param code Authorization 코드
     * @return 토큰 응답 (accessToken, refreshToken)
     */
    public TokenResponse exchangeToken(String code) {
        return oAuthApi.exchangeToken(code);
    }

    /**
     * Refresh Token으로 새로운 Access 토큰과 Refresh 토큰 발급
     *
     * @param refreshToken Refresh 토큰
     * @return 새로운 토큰 응답
     */
    public TokenResponse refreshToken(String refreshToken) {
        return oAuthApi.refreshToken(refreshToken);
    }

    /**
     * Access 토큰으로 현재 로그인한 사용자 정보 조회
     *
     * @param accessToken Access 토큰
     * @return 사용자 정보
     */
    public UserInfo getUserInfo(String accessToken) {
        return accountApi.getUserInfo(accessToken);
    }

    /**
     * 리소스 정리
     */
    @Override
    public void close() {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    /**
     * DataGsmOAuthClient 빌더
     *
     * @param clientId 클라이언트 ID
     * @param clientSecret 클라이언트 Secret
     * @return 빌더
     */
    public static Builder builder(String clientId, String clientSecret) {
        return new Builder(clientId, clientSecret);
    }

    /**
     * 빌더 클래스
     */
    public static class Builder {
        private final String clientId;
        private final String clientSecret;
        private String authorizationBaseUrl;
        private String userInfoBaseUrl;
        private HttpClient httpClient;

        private Builder(String clientId, String clientSecret) {
            if (clientId == null || clientId.trim().isEmpty()) {
                throw new IllegalArgumentException("Client id is required");
            }
            if (clientSecret == null || clientSecret.trim().isEmpty()) {
                throw new IllegalArgumentException("Client secret is required");
            }
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        /**
         * 인증 서버 베이스 URL 설정 (선택)
         *
         * @param authorizationBaseUrl 베이스 URL
         * @return 빌더
         */
        public Builder authorizationBaseUrl(String authorizationBaseUrl) {
            this.authorizationBaseUrl = authorizationBaseUrl;
            return this;
        }

        /**
         * 유저 정보 서버 베이스 URL 설정 (선택)
         *
         * @param userInfoBaseUrl 베이스 URL
         * @return 빌더
         */
        public Builder userInfoBaseUrl(String userInfoBaseUrl) {
            this.userInfoBaseUrl = userInfoBaseUrl;
            return this;
        }

        /**
         * HTTP 클라이언트 설정 (선택)
         *
         * @param httpClient HTTP 클라이언트
         * @return 빌더
         */
        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        /**
         * DataGsmClient 생성
         *
         * @return DataGsmClient
         */
        public DataGsmClient build() {
            return new DataGsmClient(this);
        }
    }
}
