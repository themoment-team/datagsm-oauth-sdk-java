package team.themoment.datagsm.sdk.oauth;

import team.themoment.datagsm.sdk.oauth.client.AccountApi;
import team.themoment.datagsm.sdk.oauth.client.AccountApiImpl;
import team.themoment.datagsm.sdk.oauth.client.OAuthApi;
import team.themoment.datagsm.sdk.oauth.client.OAuthApiImpl;
import team.themoment.datagsm.sdk.oauth.http.HttpClient;
import team.themoment.datagsm.sdk.oauth.http.OkHttpClientImpl;
import team.themoment.datagsm.sdk.oauth.model.*;

/**
 * DataGSM OAuth SDK 메인 클라이언트
 * OAuth 2.0 표준 플로우 지원 (RFC 6749)
 * PKCE 확장 지원 (RFC 7636)
 */
public class DataGsmOAuthClient implements AutoCloseable {
    private static final String DEFAULT_AUTHORIZATION_BASE_URL = "https://oauth.authorization.datagsm.kr";
    private static final String DEFAULT_USERINFO_BASE_URL = "https://oauth.resource.datagsm.kr";

    private final HttpClient httpClient;
    private final OAuthApi oAuthApi;
    private final AccountApi accountApi;

    private DataGsmOAuthClient(Builder builder) {
        this.httpClient = builder.httpClient != null ? builder.httpClient : new OkHttpClientImpl();

        String authorizationBaseUrl = builder.authorizationBaseUrl != null ? builder.authorizationBaseUrl : DEFAULT_AUTHORIZATION_BASE_URL;
        String userInfoBaseUrl = builder.userInfoBaseUrl != null ? builder.userInfoBaseUrl : DEFAULT_USERINFO_BASE_URL;

        this.oAuthApi = new OAuthApiImpl(httpClient, builder.clientId, builder.clientSecret, authorizationBaseUrl);
        this.accountApi = new AccountApiImpl(httpClient, userInfoBaseUrl);
    }

    // ==================== OAuth 2.0 Authorization Flow ====================

    /**
     * Authorization URL 생성 헬퍼
     * 사용자를 이 URL로 리다이렉트하여 인증 시작
     *
     * @param redirectUri 인증 후 돌아올 Redirect URI (클라이언트에 등록된 URI여야 함)
     * @return AuthorizationUrlBuilder (PKCE, state 등 추가 설정 가능)
     *
     * <pre>
     * AuthorizationUrlBuilder builder = client.createAuthorizationUrl("https://myapp.com/callback");
     *
     * // PKCE 활성화 (권장)
     * builder.enablePkce();
     *
     * // State 파라미터 설정 (CSRF 방지)
     * builder.state("random-state-string");
     *
     * // Scope 설정
     * builder.scope("read write");
     *
     * // URL 생성
     * String authUrl = builder.build();
     *
     * // PKCE 사용 시 Code Verifier 저장 (토큰 교환 시 필요)
     * String codeVerifier = builder.getCodeVerifier();
     * </pre>
     */
    public AuthorizationUrlBuilder createAuthorizationUrl(String redirectUri) {
        return oAuthApi.createAuthorizationUrl(redirectUri);
    }

    /**
     * Authorization Code를 Access Token으로 교환 (Authorization Code Grant)
     *
     * @param code        사용자 인증 후 받은 Authorization Code
     * @param redirectUri 원본 Authorization 요청에 사용한 Redirect URI
     * @return 토큰 응답 (access_token, refresh_token, expires_in 등)
     */
    public TokenResponse exchangeCodeForToken(String code, String redirectUri) {
        return oAuthApi.exchangeCodeForToken(code, redirectUri);
    }

    /**
     * Authorization Code를 Access Token으로 교환 (PKCE 포함)
     *
     * @param code         사용자 인증 후 받은 Authorization Code
     * @param redirectUri  원본 Authorization 요청에 사용한 Redirect URI
     * @param codeVerifier Authorization URL 생성 시 생성한 Code Verifier
     * @return 토큰 응답
     */
    public TokenResponse exchangeCodeForToken(String code, String redirectUri, String codeVerifier) {
        return oAuthApi.exchangeCodeForToken(code, redirectUri, codeVerifier);
    }

    /**
     * Refresh Token으로 새로운 Access Token 발급 (Refresh Token Grant)
     *
     * @param refreshToken 이전에 발급받은 Refresh Token
     * @return 새로운 토큰 응답 (새로운 access_token과 refresh_token 포함)
     */
    public TokenResponse refreshToken(String refreshToken) {
        return oAuthApi.refreshToken(refreshToken);
    }

    /**
     * Refresh Token으로 새로운 Access Token 발급 (scope 지정)
     *
     * @param refreshToken 이전에 발급받은 Refresh Token
     * @param scope        요청할 권한 범위 (원본보다 좁아야 함)
     * @return 새로운 토큰 응답
     */
    public TokenResponse refreshToken(String refreshToken, String scope) {
        return oAuthApi.refreshToken(refreshToken, scope);
    }

    /**
     * Client Credentials로 Access Token 발급 (Client Credentials Grant)
     * 사용자 없이 클라이언트 자격증명만으로 토큰 발급
     *
     * @return 토큰 응답 (refresh_token 없음)
     */
    public TokenResponse getClientCredentialsToken() {
        return oAuthApi.getClientCredentialsToken();
    }

    /**
     * Client Credentials로 Access Token 발급 (scope 지정)
     *
     * @param scope 요청할 권한 범위
     * @return 토큰 응답
     */
    public TokenResponse getClientCredentialsToken(String scope) {
        return oAuthApi.getClientCredentialsToken(scope);
    }

    // ==================== Legacy API ====================

    /**
     * OAuth Code 발급 (레거시 엔드포인트)
     *
     * @deprecated 표준 OAuth 2.0 플로우를 사용하세요:
     *             createAuthorizationUrl() → 사용자 인증 → exchangeCodeForToken()
     * @param request OAuth Code 요청
     * @return OAuth Code 응답
     */
    @Deprecated
    public OAuthCodeResponse issueOAuthCode(OAuthCodeRequest request) {
        return oAuthApi.issueOAuthCode(request);
    }

    // ==================== User Info ====================

    /**
     * Access Token으로 현재 로그인한 사용자 정보 조회
     *
     * @param accessToken Access Token
     * @return 사용자 정보
     */
    public UserInfo getUserInfo(String accessToken) {
        return accountApi.getUserInfo(accessToken);
    }

    // ==================== Resource Management ====================

    /**
     * 리소스 정리
     */
    @Override
    public void close() {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    // ==================== Builder ====================

    /**
     * DataGsmOAuthClient 빌더
     *
     * @param clientId     클라이언트 ID
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
         * DataGsmOAuthClient 생성
         *
         * @return DataGsmOAuthClient
         */
        public DataGsmOAuthClient build() {
            return new DataGsmOAuthClient(this);
        }
    }
}
