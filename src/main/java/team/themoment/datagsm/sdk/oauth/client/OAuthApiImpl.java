package team.themoment.datagsm.sdk.oauth.client;

import team.themoment.datagsm.sdk.oauth.http.HttpClient;
import team.themoment.datagsm.sdk.oauth.http.JsonUtil;
import team.themoment.datagsm.sdk.oauth.model.*;
import team.themoment.datagsm.sdk.oauth.util.ScopeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OAuth 2.0 API 구현
 * RFC 6749 (OAuth 2.0), RFC 7636 (PKCE) 표준 준수
 */
public class OAuthApiImpl implements OAuthApi {
    private final HttpClient httpClient;
    private final String clientId;
    private final String clientSecret;
    private final String baseUrl;

    public OAuthApiImpl(HttpClient httpClient, String clientId, String clientSecret, String baseUrl) {
        this.httpClient = httpClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.baseUrl = baseUrl;
    }

    @Override
    public AuthorizationUrlBuilder createAuthorizationUrl(String redirectUri) {
        String authorizationEndpoint = baseUrl + "/v1/oauth/authorize";
        return new AuthorizationUrlBuilder(authorizationEndpoint, clientId)
                .redirectUri(redirectUri);
    }

    @Override
    public TokenResponse exchangeCodeForToken(String code, String redirectUri) {
        return exchangeCodeForToken(code, redirectUri, null);
    }

    @Override
    public TokenResponse exchangeCodeForToken(String code, String redirectUri, String codeVerifier) {
        Map<String, String> headers = createRequestHeaders();

        Map<String, Object> body = new HashMap<>();
        body.put("grant_type", "authorization_code");
        body.put("code", code);
        body.put("client_id", clientId);
        body.put("redirect_uri", redirectUri);

        // PKCE 사용 시 client_secret 없이 code_verifier만 전송
        if (codeVerifier != null && !codeVerifier.isEmpty()) {
            body.put("code_verifier", codeVerifier);
        } else {
            // PKCE 미사용 시 client_secret 필수
            body.put("client_secret", clientSecret);
        }

        String responseBody = httpClient.post(
                baseUrl + "/v1/oauth/token",
                headers,
                JsonUtil.toJson(body)
        );

        return JsonUtil.fromJson(responseBody, TokenResponse.class);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        return refreshToken(refreshToken, (List<String>) null);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken, String scope) {
        return refreshToken(refreshToken, ScopeUtils.split(scope));
    }

    @Override
    public TokenResponse refreshToken(String refreshToken, List<String> scopes) {
        Map<String, String> headers = createRequestHeaders();

        Map<String, Object> body = new HashMap<>();
        body.put("grant_type", "refresh_token");
        body.put("refresh_token", refreshToken);
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);

        putScopes(body, scopes);

        String responseBody = httpClient.post(
                baseUrl + "/v1/oauth/token",
                headers,
                JsonUtil.toJson(body)
        );

        return JsonUtil.fromJson(responseBody, TokenResponse.class);
    }

    @Override
    public TokenResponse getClientCredentialsToken() {
        return getClientCredentialsToken((List<String>) null);
    }

    @Override
    public TokenResponse getClientCredentialsToken(String scope) {
        return getClientCredentialsToken(ScopeUtils.split(scope));
    }

    @Override
    public TokenResponse getClientCredentialsToken(List<String> scopes) {
        Map<String, String> headers = createRequestHeaders();

        Map<String, Object> body = new HashMap<>();
        body.put("grant_type", "client_credentials");
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);

        putScopes(body, scopes);

        String responseBody = httpClient.post(
                baseUrl + "/v1/oauth/token",
                headers,
                JsonUtil.toJson(body)
        );

        return JsonUtil.fromJson(responseBody, TokenResponse.class);
    }

    /**
     * 서버의 Oauth2TokenReqDto.scope는 Set&lt;String&gt;이므로 JSON 배열로 전송합니다.
     * 비어 있으면 필드를 생략해 서버 기본 동작(원본 scope 유지 또는 클라이언트 전체 scope)을 따릅니다.
     */
    private static void putScopes(Map<String, Object> body, List<String> scopes) {
        List<String> normalized = ScopeUtils.normalize(scopes);
        if (!normalized.isEmpty()) {
            body.put("scope", normalized);
        }
    }

    /**
     * Form Content-Type 헤더 생성
     * OAuth 2.0 표준은 application/x-www-form-urlencoded 또는 application/json 지원
     */
    private Map<String, String> createRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }
}