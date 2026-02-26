package team.themoment.datagsm.sdk.oauth.client;

import team.themoment.datagsm.sdk.oauth.model.*;

/**
 * OAuth 2.0 API 인터페이스
 * RFC 6749 (OAuth 2.0), RFC 7636 (PKCE) 표준 준수
 */
public interface OAuthApi {

    /**
     * Authorization URL 생성 헬퍼
     * 
     * @param redirectUri Redirect URI
     * @return AuthorizationUrlBuilder
     */
    AuthorizationUrlBuilder createAuthorizationUrl(String redirectUri);

    /**
     * Authorization Code를 Access Token으로 교환 (Authorization Code Grant)
     * 
     * @param code Authorization Code
     * @param redirectUri 원본 요청에 사용된 Redirect URI
     * @return 토큰 응답
     */
    TokenResponse exchangeCodeForToken(String code, String redirectUri);

    /**
     * Authorization Code를 Access Token으로 교환 (PKCE 포함)
     * 
     * @param code Authorization Code
     * @param redirectUri 원본 요청에 사용된 Redirect URI
     * @param codeVerifier PKCE Code Verifier
     * @return 토큰 응답
     */
    TokenResponse exchangeCodeForToken(String code, String redirectUri, String codeVerifier);

    /**
     * Refresh Token으로 새로운 Access Token 발급 (Refresh Token Grant)
     *
     * @param refreshToken Refresh Token
     * @return 새로운 토큰 응답
     */
    TokenResponse refreshToken(String refreshToken);

    /**
     * Refresh Token으로 새로운 Access Token 발급 (scope 지정)
     *
     * @param refreshToken Refresh Token
     * @param scope 요청할 권한 범위 (선택, 원본보다 좁아야 함)
     * @return 새로운 토큰 응답
     */
    TokenResponse refreshToken(String refreshToken, String scope);

    /**
     * Client Credentials로 Access Token 발급 (Client Credentials Grant)
     * 사용자 없이 클라이언트 자격증명만으로 토큰 발급
     *
     * @return 토큰 응답 (refresh_token 없음)
     */
    TokenResponse getClientCredentialsToken();

    /**
     * Client Credentials로 Access Token 발급 (scope 지정)
     *
     * @param scope 요청할 권한 범위
     * @return 토큰 응답
     */
    TokenResponse getClientCredentialsToken(String scope);

    /**
     * OAuth Code 발급 (레거시 엔드포인트)
     * 
     * @deprecated 표준 OAuth 2.0 플로우를 사용하세요 (createAuthorizationUrl → exchangeCodeForToken)
     * @param request OAuth Code 요청
     * @return OAuth Code 응답
     */
    @Deprecated
    OAuthCodeResponse issueOAuthCode(OAuthCodeRequest request);
}
