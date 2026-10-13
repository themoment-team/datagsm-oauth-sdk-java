package team.themoment.datagsm.sdk.oauth.example;

import team.themoment.datagsm.sdk.oauth.DataGsmOAuthClient;
import team.themoment.datagsm.sdk.oauth.exception.DataGsmException;
import team.themoment.datagsm.sdk.oauth.model.AccountObjectType;
import team.themoment.datagsm.sdk.oauth.model.AuthorizationUrlBuilder;
import team.themoment.datagsm.sdk.oauth.model.ClubSummary;
import team.themoment.datagsm.sdk.oauth.model.OidcDiscoveryDocument;
import team.themoment.datagsm.sdk.oauth.model.ProjectSummary;
import team.themoment.datagsm.sdk.oauth.model.Student;
import team.themoment.datagsm.sdk.oauth.model.StudentRole;
import team.themoment.datagsm.sdk.oauth.model.Teacher;
import team.themoment.datagsm.sdk.oauth.model.TokenResponse;
import team.themoment.datagsm.sdk.oauth.model.UserInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * DataGSM OAuth SDK 사용 예제
 * <p>
 * scope는 {@code {appId}:{scopeName}} 형식입니다. {@code {appId}}는 DataGSM 애플리케이션 ID이며,
 * 실제 값은 {@link DataGsmOAuthClient#getDiscoveryDocument()}의 {@code scopes_supported}에서 확인할 수 있습니다.
 */
public class OAuthExample {
    private static final String APP_ID = "your-datagsm-app-id";

    private static final String OPENID = "openid";
    private static final String ACCOUNT_READ = APP_ID + ":account_read";
    private static final String STUDENT_READ = APP_ID + ":student_read";
    private static final String CLUB_READ = APP_ID + ":club_read";
    private static final String PROJECT_READ = APP_ID + ":project_read";

    public static void main(String[] args) {
        // Confidential client: 서버에서 client_secret을 안전하게 보관할 수 있는 경우
        try (DataGsmOAuthClient client = DataGsmOAuthClient.builder("your-client-id", "your-client-secret").build()) {
            discovery(client);
            standardOAuthFlow(client);
            refreshTokenFlow(client);
            clientCredentialsFlow(client);
        } catch (DataGsmException e) {
            // OAuth 에러면 errorCode = invalid_grant 등, CommonApiResponse 에러면 errorCode = UNAUTHORIZED 등
            System.out.println("요청 실패: status=" + e.getStatusCode()
                    + ", errorCode=" + e.getErrorCode()
                    + ", description=" + e.getErrorDescription());
        }

        // Public client: 모바일/SPA처럼 secret을 보관할 수 없는 경우 (PKCE 필수)
        try (DataGsmOAuthClient publicClient = DataGsmOAuthClient.builder("your-public-client-id").build()) {
            pkceOAuthFlow(publicClient);
        } catch (DataGsmException e) {
            System.out.println("요청 실패: " + e.getMessage());
        }
    }

    /**
     * OIDC Discovery 문서로 엔드포인트와 지원 scope 확인
     */
    private static void discovery(DataGsmOAuthClient client) {
        System.out.println("=== OIDC Discovery ===");
        OidcDiscoveryDocument document = client.getDiscoveryDocument();
        System.out.println("issuer: " + document.getIssuer());
        System.out.println("scopes_supported: " + document.getScopesSupported());
    }

    /**
     * 표준 OAuth 2.0 Authorization Code Flow (+ OIDC)
     */
    private static void standardOAuthFlow(DataGsmOAuthClient client) {
        System.out.println("\n=== Authorization Code Flow ===");

        // Step 1: Authorization URL 생성. state와 nonce는 세션에 저장해 두고 콜백에서 비교합니다.
        String redirectUri = "https://myapp.com/callback";
        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();

        AuthorizationUrlBuilder urlBuilder = client.createAuthorizationUrl(redirectUri)
                .state(state)
                .nonce(nonce)
                .scopes(Arrays.asList(OPENID, ACCOUNT_READ, STUDENT_READ, CLUB_READ, PROJECT_READ));

        System.out.println("1. 사용자를 이 URL로 리다이렉트: " + urlBuilder.build());

        // Step 2: 콜백으로 받은 Authorization Code (예: https://myapp.com/callback?code=abc123&state=...)
        String authorizationCode = "received-authorization-code";

        // Step 3: Authorization Code를 Access Token으로 교환
        TokenResponse tokenResponse = client.exchangeCodeForToken(authorizationCode, redirectUri);
        System.out.println("2. 부여된 scope: " + tokenResponse.getScopes());
        // openid scope를 요청했을 때만 발급됩니다. SDK는 서명을 검증하지 않으므로 JWT 라이브러리로 직접 검증하세요.
        System.out.println("   id_token 발급 여부: " + (tokenResponse.getIdToken() != null));

        // Step 4: Access Token으로 사용자 정보 조회
        printUserInfo(client.getUserInfo(tokenResponse.getAccessToken()));
    }

    /**
     * scope에 따라 비어 있을 수 있는 필드를 null-safe하게 다루는 예시
     */
    private static void printUserInfo(UserInfo userInfo) {
        System.out.println("3. User Info");
        System.out.println("   - Email: " + userInfo.getEmail());

        // student/teacher는 student_read scope가 없거나 해당 계정 종류가 아니면 null입니다.
        Student student = userInfo.getStudent();
        Teacher teacher = userInfo.getTeacher();
        if (userInfo.getObjectType() == AccountObjectType.STUDENT && student != null) {
            System.out.println("   - 학생: " + student.getName()
                    + (student.getRole() == StudentRole.WITHDRAWN ? " (자퇴)" : ""));
            // 졸업생·자퇴생은 학번이 null일 수 있고, 동아리는 club_read scope가 없으면 null입니다.
            ClubSummary majorClub = student.getMajorClub();
            System.out.println("   - 학번: " + (student.getStudentNumber() != null ? student.getStudentNumber() : "없음"));
            System.out.println("   - 전공 동아리: " + (majorClub != null ? majorClub.getName() : "없음"));
        } else if (userInfo.getObjectType() == AccountObjectType.TEACHER && teacher != null) {
            System.out.println("   - 선생님: " + teacher.getName());
        } else {
            System.out.println("   - 연결된 학생/선생님 정보 없음 (또는 student_read scope 없음)");
        }

        // clubs/projects는 scope가 없으면 빈 목록이며 null이 아닙니다.
        for (ClubSummary club : userInfo.getClubs()) {
            System.out.println("   - 동아리: " + club.getName() + " (" + club.getType() + ")");
        }
        for (ProjectSummary project : userInfo.getProjects()) {
            ClubSummary owner = project.getClub();
            System.out.println("   - 프로젝트: " + project.getName() + " [" + project.getStatus() + "]"
                    + (owner != null ? " @" + owner.getName() : ""));
        }
    }

    /**
     * PKCE를 사용한 OAuth Flow (public client)
     */
    private static void pkceOAuthFlow(DataGsmOAuthClient publicClient) {
        System.out.println("\n=== PKCE Flow (public client) ===");

        String redirectUri = "myapp://callback";
        AuthorizationUrlBuilder urlBuilder = publicClient.createAuthorizationUrl(redirectUri)
                .enablePkce() // S256 Code Verifier 자동 생성
                .state(UUID.randomUUID().toString())
                .scope(ACCOUNT_READ + " " + STUDENT_READ);

        System.out.println("1. Authorization URL: " + urlBuilder.build());
        String codeVerifier = urlBuilder.getCodeVerifier(); // 토큰 교환까지 보관해야 합니다.

        String authorizationCode = "received-code-from-callback";

        // public client는 반드시 code_verifier와 함께 교환합니다. (client_secret 없이 전송)
        TokenResponse tokenResponse = publicClient.exchangeCodeForToken(authorizationCode, redirectUri, codeVerifier);
        System.out.println("2. Access Token 발급 완료, 만료(초): " + tokenResponse.getExpiresIn());

        // public client의 refresh 요청도 client_secret 없이 전송됩니다.
        if (tokenResponse.getRefreshToken() != null) {
            publicClient.refreshToken(tokenResponse.getRefreshToken());
        }
    }

    /**
     * Refresh Token으로 토큰 갱신
     */
    private static void refreshTokenFlow(DataGsmOAuthClient client) {
        System.out.println("\n=== Refresh Token Flow ===");

        String existingRefreshToken = "your-refresh-token";

        // 방법 1: 기존 scope 그대로 갱신
        TokenResponse newTokens = client.refreshToken(existingRefreshToken);
        System.out.println("1. 갱신된 scope: " + newTokens.getScopes());

        // 방법 2: 기존 scope 중 일부만 요청 (다운스코핑). 기존 범위를 넘으면 invalid_scope 에러가 납니다.
        List<String> narrowed = Collections.singletonList(ACCOUNT_READ);
        TokenResponse limitedTokens = client.refreshToken(existingRefreshToken, narrowed);
        System.out.println("2. 축소된 scope: " + limitedTokens.getScopes());
    }

    /**
     * Client Credentials Grant (서버 간 통신, confidential client 전용)
     */
    private static void clientCredentialsFlow(DataGsmOAuthClient client) {
        System.out.println("\n=== Client Credentials Flow ===");

        // scope를 생략하면 클라이언트에 등록된 scope 전체가 부여됩니다.
        TokenResponse tokenResponse = client.getClientCredentialsToken(Collections.singletonList(ACCOUNT_READ));
        System.out.println("1. 부여된 scope: " + tokenResponse.getScopes());
        System.out.println("2. Refresh Token: " + tokenResponse.getRefreshToken()); // 항상 null
    }
}
