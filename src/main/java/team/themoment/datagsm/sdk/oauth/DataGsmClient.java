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
    private static final String DEFAULT_BASE_URL = "https://api.datagsm.com";

    private final HttpClient httpClient;
    private final OAuthApi oAuthApi;
    private final AccountApi accountApi;

    private DataGsmClient(Builder builder) {
        this.httpClient = builder.httpClient != null ? builder.httpClient : new OkHttpClientImpl();

        String baseUrl = builder.baseUrl != null ? builder.baseUrl : DEFAULT_BASE_URL;

        this.oAuthApi = new OAuthApiImpl(httpClient, builder.clientSecret, baseUrl);
        this.accountApi = new AccountApiImpl(httpClient, baseUrl);
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
     * Authorization 헤더 값 생성
     *
     * @deprecated 사용 보류중인 메서드입니다.
     * @param accessToken Access Token
     * @return "Bearer {accessToken}" 형식의 헤더 값
     */
    @Deprecated
    public String buildAuthorizationHeader(String accessToken) {
        return "Bearer " + accessToken;
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
     * @param clientSecret 클라이언트 Secret
     * @return 빌더
     */
    public static Builder builder(String clientSecret) {
        return new Builder(clientSecret);
    }

    /**
     * 빌더 클래스
     */
    public static class Builder {
        private final String clientSecret;
        private String baseUrl;
        private HttpClient httpClient;

        private Builder(String clientSecret) {
            if (clientSecret == null || clientSecret.trim().isEmpty()) {
                throw new IllegalArgumentException("Client secret is required");
            }
            this.clientSecret = clientSecret;
        }

        /**
         * 베이스 URL 설정 (선택)
         *
         * @param baseUrl 베이스 URL
         * @return 빌더
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
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
