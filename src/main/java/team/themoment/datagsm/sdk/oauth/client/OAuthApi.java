package team.themoment.datagsm.sdk.oauth.client;

import team.themoment.datagsm.sdk.oauth.model.*;

import java.util.List;

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
     * @param scope 공백으로 구분된 권한 범위 (선택, 원본보다 좁아야 함). 내부적으로 분리해 배열로 전송합니다.
     * @return 새로운 토큰 응답
     */
    TokenResponse refreshToken(String refreshToken, String scope);

    /**
     * Refresh Token으로 새로운 Access Token 발급 (scope 목록 지정)
     *
     * @param refreshToken Refresh Token
     * @param scopes 요청할 권한 범위 목록 (선택, 원본보다 좁아야 함). null이거나 비어 있으면 원본 scope를 유지합니다.
     * @return 새로운 토큰 응답
     */
    TokenResponse refreshToken(String refreshToken, List<String> scopes);

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
     * @param scope 공백으로 구분된 권한 범위. 내부적으로 분리해 배열로 전송합니다.
     * @return 토큰 응답
     */
    TokenResponse getClientCredentialsToken(String scope);

    /**
     * Client Credentials로 Access Token 발급 (scope 목록 지정)
     *
     * @param scopes 요청할 권한 범위 목록. null이거나 비어 있으면 클라이언트에 등록된 전체 scope가 부여됩니다.
     * @return 토큰 응답
     */
    TokenResponse getClientCredentialsToken(List<String> scopes);
}
