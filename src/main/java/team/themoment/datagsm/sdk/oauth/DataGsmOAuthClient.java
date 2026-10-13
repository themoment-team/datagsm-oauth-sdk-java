package team.themoment.datagsm.sdk.oauth;

import team.themoment.datagsm.sdk.oauth.client.AccountApi;
import team.themoment.datagsm.sdk.oauth.client.AccountApiImpl;
import team.themoment.datagsm.sdk.oauth.client.OAuthApi;
import team.themoment.datagsm.sdk.oauth.client.OAuthApiImpl;
import team.themoment.datagsm.sdk.oauth.http.HttpClient;
import team.themoment.datagsm.sdk.oauth.http.OkHttpClientImpl;
import team.themoment.datagsm.sdk.oauth.model.*;

import java.util.List;

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
     * // Scope 설정 ({appId}:{scopeName} 형식, OIDC는 openid 추가)
     * builder.scope("openid my-app-id:account_read my-app-id:student_read");
     *
     * // OIDC nonce (openid 요청 시 권장)
     * builder.nonce("random-nonce");
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
     * @param scope        공백으로 구분된 권한 범위 (원본보다 좁아야 함). 분리해서 배열로 전송합니다.
     * @return 새로운 토큰 응답
     */
    public TokenResponse refreshToken(String refreshToken, String scope) {
        return oAuthApi.refreshToken(refreshToken, scope);
    }

    /**
     * Refresh Token으로 새로운 Access Token 발급 (scope 목록 지정)
     *
     * <pre>
     * client.refreshToken(refreshToken, List.of("my-app-id:account_read"));
     * </pre>
     *
     * @param refreshToken 이전에 발급받은 Refresh Token
     * @param scopes       요청할 권한 범위 목록 (원본보다 좁아야 함). null이거나 비어 있으면 원본 scope를 유지합니다.
     * @return 새로운 토큰 응답
     */
    public TokenResponse refreshToken(String refreshToken, List<String> scopes) {
        return oAuthApi.refreshToken(refreshToken, scopes);
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
     * @param scope 공백으로 구분된 권한 범위. 분리해서 배열로 전송합니다.
     * @return 토큰 응답
     */
    public TokenResponse getClientCredentialsToken(String scope) {
        return oAuthApi.getClientCredentialsToken(scope);
    }

    /**
     * Client Credentials로 Access Token 발급 (scope 목록 지정)
     *
     * @param scopes 요청할 권한 범위 목록. null이거나 비어 있으면 클라이언트에 등록된 전체 scope가 부여됩니다.
     * @return 토큰 응답
     */
    public TokenResponse getClientCredentialsToken(List<String> scopes) {
        return oAuthApi.getClientCredentialsToken(scopes);
    }

    // ==================== OpenID Connect ====================

    /**
     * OIDC Discovery 문서 조회 ({@code GET /.well-known/openid-configuration})
     *
     * @return issuer, 엔드포인트, 지원 scope/알고리즘 등 메타데이터
     */
    public OidcDiscoveryDocument getDiscoveryDocument() {
        return oAuthApi.getDiscoveryDocument();
    }

    /**
     * id_token 서명 검증용 JWK Set 조회 ({@code GET /v1/oauth/jwks})
     * <p>
     * SDK는 id_token 서명을 검증하지 않습니다. 이 키로 JWT 라이브러리에서 RS256 서명과
     * iss, aud(client_id), exp, nonce를 직접 검증하세요.
     *
     * @return JWK Set
     */
    public JsonWebKeySet getJwkSet() {
        return oAuthApi.getJwkSet();
    }

    /**
     * IdP 세션 로그아웃 엔드포인트 URL ({@code POST /v1/oauth/logout})
     * <p>
     * 서버는 요청 본문 없이 브라우저의 IdP 세션 쿠키로만 세션을 식별하고 항상 204를 반환합니다.
     * SP 서버에서 호출하면 쿠키가 없어 아무 효과가 없으므로, 브라우저에서
     * {@code fetch(url, { method: "POST", credentials: "include" })}로 호출하세요.
     * 이미 발급된 access token은 만료될 때까지 유효합니다.
     *
     * @return 로그아웃 엔드포인트 URL
     */
    public String getLogoutEndpoint() {
        return oAuthApi.getLogoutEndpoint();
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
     * Confidential client 빌더 (client_secret 보유)
     *
     * @param clientId     클라이언트 ID
     * @param clientSecret 클라이언트 Secret (null 또는 빈 문자열 불가)
     * @return 빌더
     */
    public static Builder builder(String clientId, String clientSecret) {
        if (clientSecret == null || clientSecret.trim().isEmpty()) {
            throw new IllegalArgumentException("Client secret is required. Use builder(clientId) for public clients");
        }
        return new Builder(clientId, clientSecret);
    }

    /**
     * Public client 빌더 (client_secret 없음, PKCE 필수)
     * <p>
     * 모바일·SPA·데스크톱처럼 secret을 안전하게 보관할 수 없는 환경에서 사용합니다.
     * 서버 Discovery의 {@code token_endpoint_auth_methods_supported}에 {@code none}이 포함되어 있습니다.
     * <ul>
     *     <li>authorization_code 교환은 반드시 {@code code_verifier}와 함께 호출해야 합니다.</li>
     *     <li>refresh_token 갱신은 client_secret 없이 요청합니다.</li>
     *     <li>client_credentials grant는 사용할 수 없습니다.</li>
     * </ul>
     *
     * @param clientId 클라이언트 ID
     * @return 빌더
     */
    public static Builder builder(String clientId) {
        return new Builder(clientId, null);
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
