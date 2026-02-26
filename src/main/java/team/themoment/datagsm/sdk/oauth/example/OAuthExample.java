package team.themoment.datagsm.sdk.oauth.example;

import team.themoment.datagsm.sdk.oauth.DataGsmClient;
import team.themoment.datagsm.sdk.oauth.model.AuthorizationUrlBuilder;
import team.themoment.datagsm.sdk.oauth.model.TokenResponse;
import team.themoment.datagsm.sdk.oauth.model.UserInfo;

/**
 * DataGSM OAuth SDK 사용 예제
 */
public class OAuthExample {

    public static void main(String[] args) {
        // 1. DataGsmClient 생성
        DataGsmClient client = DataGsmClient.builder("your-client-id", "your-client-secret")
                .authorizationBaseUrl("https://oauth.data.hellogsm.kr")
                .userInfoBaseUrl("https://oauth-userinfo.data.hellogsm.kr")
                .build();

        try {
            // ================= 표준 OAuth 2.0 Authorization Code Flow =================
            standardOAuthFlow(client);

            // ================= PKCE 사용 OAuth Flow (Public Client) =================
            pkceOAuthFlow(client);

            // ================= Refresh Token으로 토큰 갱신 =================
            refreshTokenFlow(client);

            // ================= Client Credentials Grant =================
            clientCredentialsFlow(client);

        } finally {
            client.close();
        }
    }

    /**
     * 표준 OAuth 2.0 Authorization Code Flow
     */
    private static void standardOAuthFlow(DataGsmClient client) {
        System.out.println("=== Standard OAuth 2.0 Flow ===");

        // Step 1: Authorization URL 생성
        String redirectUri = "https://myapp.com/callback";
        String state = "random-csrf-token"; // CSRF 방지용

        AuthorizationUrlBuilder urlBuilder = client.createAuthorizationUrl(redirectUri)
                .state(state)
                .scope("read write");

        String authorizationUrl = urlBuilder.build();
        System.out.println("1. 사용자를 이 URL로 리다이렉트: " + authorizationUrl);

        // Step 2: 사용자가 로그인/승인 후 Redirect URI로 Authorization Code를 받음
        // 예: https://myapp.com/callback?code=abc123&state=random-csrf-token

        String authorizationCode = "received-authorization-code"; // 실제로는 콜백에서 받음

        // Step 3: Authorization Code를 Access Token으로 교환
        TokenResponse tokenResponse = client.exchangeCodeForToken(authorizationCode, redirectUri);

        System.out.println("2. Token Response:");
        System.out.println("   - Access Token: " + tokenResponse.getAccessToken());
        System.out.println("   - Token Type: " + tokenResponse.getTokenType());
        System.out.println("   - Expires In: " + tokenResponse.getExpiresIn() + " seconds");
        System.out.println("   - Refresh Token: " + tokenResponse.getRefreshToken());
        System.out.println("   - Scope: " + tokenResponse.getScope());

        // Step 4: Access Token으로 사용자 정보 조회
        UserInfo userInfo = client.getUserInfo(tokenResponse.getAccessToken());
        System.out.println("3. User Info:");
        System.out.println("   - Email: " + userInfo.getEmail());
        System.out.println("   - Name: " + (userInfo.isStudent() ? userInfo.getStudent().getName() : "N/A"));
    }

    /**
     * PKCE를 사용한 OAuth Flow (모바일/SPA 앱 권장)
     */
    private static void pkceOAuthFlow(DataGsmClient client) {
        System.out.println("\n=== PKCE OAuth Flow ===");

        // Step 1: PKCE가 활성화된 Authorization URL 생성
        String redirectUri = "myapp://callback";

        AuthorizationUrlBuilder urlBuilder = client.createAuthorizationUrl(redirectUri)
                .enablePkce() // PKCE 자동 생성 (S256 메소드)
                .state("random-state");

        String authorizationUrl = urlBuilder.build();
        String codeVerifier = urlBuilder.getCodeVerifier(); // 반드시 저장!

        System.out.println("1. Authorization URL: " + authorizationUrl);
        System.out.println("2. Code Verifier (저장 필요): " + codeVerifier);

        // Step 2: Authorization Code 받음
        String authorizationCode = "received-code-from-callback";

        // Step 3: Code Verifier와 함께 토큰 교환 (client_secret 불필요)
        TokenResponse tokenResponse = client.exchangeCodeForToken(
                authorizationCode,
                redirectUri,
                codeVerifier // PKCE 검증용
        );

        System.out.println("3. Access Token 획득: " + tokenResponse.getAccessToken());
    }

    /**
     * Refresh Token으로 토큰 갱신
     */
    private static void refreshTokenFlow(DataGsmClient client) {
        System.out.println("\n=== Refresh Token Flow ===");

        String existingRefreshToken = "your-refresh-token";

        // 방법 1: 기본 갱신 (원본 scope 유지)
        TokenResponse newTokens = client.refreshToken(existingRefreshToken);

        System.out.println("1. New Access Token: " + newTokens.getAccessToken());
        System.out.println("2. New Refresh Token: " + newTokens.getRefreshToken());

        // 방법 2: Scope를 줄여서 갱신 (다운스코핑)
        TokenResponse limitedTokens = client.refreshToken(existingRefreshToken, "read");

        System.out.println("3. Limited Scope Token: " + limitedTokens.getScope());
    }

    /**
     * Client Credentials Grant (서버간 통신용)
     */
    private static void clientCredentialsFlow(DataGsmClient client) {
        System.out.println("\n=== Client Credentials Flow ===");

        // 사용자 없이 클라이언트 자격증명만으로 토큰 발급
        TokenResponse tokenResponse = client.getClientCredentialsToken("read");

        System.out.println("1. Access Token: " + tokenResponse.getAccessToken());
        System.out.println("2. Token Type: " + tokenResponse.getTokenType());
        System.out.println("3. Expires In: " + tokenResponse.getExpiresIn());
        System.out.println("4. Refresh Token: " + tokenResponse.getRefreshToken()); // null (client_credentials는 refresh 불가)
    }
}
