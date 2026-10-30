package team.themoment.datagsm.sdk.oauth.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import team.themoment.datagsm.sdk.oauth.exception.BadRequestException;
import team.themoment.datagsm.sdk.oauth.exception.UnauthorizedException;
import team.themoment.datagsm.sdk.oauth.http.OkHttpClientImpl;
import team.themoment.datagsm.sdk.oauth.model.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OAuthApiImpl")
class OAuthApiImplTest {

    private MockWebServer mockWebServer;
    private OAuthApiImpl oAuthApi;

    private static final String CLIENT_ID = "test-client";
    private static final String CLIENT_SECRET = "test-secret";
    private static final String REDIRECT_URI = "https://myapp.com/callback";

    private static final String TOKEN_RESPONSE_JSON =
            "{\"access_token\":\"test-access-token\",\"token_type\":\"Bearer\"," +
                    "\"expires_in\":3600,\"refresh_token\":\"test-refresh-token\",\"scope\":\"read\"}";

    private static final String CC_TOKEN_RESPONSE_JSON =
            "{\"access_token\":\"cc-access-token\",\"token_type\":\"Bearer\"," +
                    "\"expires_in\":3600,\"refresh_token\":null,\"scope\":\"read\"}";

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = "http://localhost:" + mockWebServer.getPort();
        oAuthApi = new OAuthApiImpl(new OkHttpClientImpl(), CLIENT_ID, CLIENT_SECRET, baseUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("createAuthorizationUrl()")
    class CreateAuthorizationUrl {

        @Test
        @DisplayName("redirectUri가 포함된 AuthorizationUrlBuilder를 반환한다")
        void returnsBuilderWithRedirectUri() {
            AuthorizationUrlBuilder builder = oAuthApi.createAuthorizationUrl(REDIRECT_URI);

            assertNotNull(builder);
            String url = builder.build();
            assertTrue(url.contains("redirect_uri="));
            assertTrue(url.contains("response_type=code"));
            assertTrue(url.contains("client_id=" + CLIENT_ID));
        }
    }

    @Nested
    @DisplayName("exchangeCodeForToken()")
    class ExchangeCodeForToken {

        @Test
        @DisplayName("Authorization Code를 Access Token으로 교환한다 (client_secret 방식)")
        void exchangeWithClientSecret() throws InterruptedException {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(TOKEN_RESPONSE_JSON));

            TokenResponse response = oAuthApi.exchangeCodeForToken("auth-code", REDIRECT_URI);

            assertEquals("test-access-token", response.getAccessToken());
            assertEquals("Bearer", response.getTokenType());
            assertEquals(3600L, response.getExpiresIn());
            assertEquals("test-refresh-token", response.getRefreshToken());
            assertEquals("read", response.getScope());

            RecordedRequest request = mockWebServer.takeRequest(3, TimeUnit.SECONDS);
            assertNotNull(request, "요청이 서버에 도달하지 않았습니다");
            String body = request.getBody().readUtf8();
            assertTrue(body.contains("\"grant_type\":\"authorization_code\""));
            assertTrue(body.contains("\"client_secret\":\"" + CLIENT_SECRET + "\""));
            assertFalse(body.contains("code_verifier"));
        }

        @Test
        @DisplayName("PKCE 사용 시 code_verifier를 전송하고 client_secret은 전송하지 않는다")
        void exchangeWithPkce() throws InterruptedException {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(TOKEN_RESPONSE_JSON));

            oAuthApi.exchangeCodeForToken("auth-code", REDIRECT_URI, "my-code-verifier");

            RecordedRequest request = mockWebServer.takeRequest(3, TimeUnit.SECONDS);
            assertNotNull(request, "요청이 서버에 도달하지 않았습니다");
            String body = request.getBody().readUtf8();
            assertTrue(body.contains("\"code_verifier\":\"my-code-verifier\""));
            assertFalse(body.contains("client_secret"));
        }

        @Test
        @DisplayName("401 응답 시 UnauthorizedException이 발생한다")
        void throwsUnauthorizedOn401() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(401).setBody("Unauthorized"));

            assertThrows(UnauthorizedException.class,
                    () -> oAuthApi.exchangeCodeForToken("bad-code", REDIRECT_URI));
        }

        @Test
        @DisplayName("OAuth 에러 응답의 error, error_description을 예외에 담는다")
        void oauthErrorParsed() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(400)
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"error\":\"invalid_grant\",\"error_description\":\"PKCE 검증에 실패했습니다.\"}"));

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> oAuthApi.exchangeCodeForToken("code", REDIRECT_URI, "wrong"));

            assertEquals("invalid_grant", ex.getErrorCode());
            assertEquals("PKCE 검증에 실패했습니다.", ex.getErrorDescription());
        }

        @Test
        @DisplayName("400 응답 시 BadRequestException이 발생한다")
        void throwsBadRequestOn400() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("Bad Request"));

            assertThrows(BadRequestException.class,
                    () -> oAuthApi.exchangeCodeForToken("", REDIRECT_URI));
        }
    }

    @Nested
    @DisplayName("refreshToken()")
    class RefreshToken {

        @Test
        @DisplayName("Refresh Token으로 새로운 Access Token을 발급받는다")
        void refreshTokenSucceeds() throws InterruptedException {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(TOKEN_RESPONSE_JSON));

            TokenResponse response = oAuthApi.refreshToken("my-refresh-token");

            assertNotNull(response.getAccessToken());

            RecordedRequest request = mockWebServer.takeRequest(3, TimeUnit.SECONDS);
            assertNotNull(request, "요청이 서버에 도달하지 않았습니다");
            String body = request.getBody().readUtf8();
            assertTrue(body.contains("\"grant_type\":\"refresh_token\""));
            assertTrue(body.contains("\"refresh_token\":\"my-refresh-token\""));
            assertTrue(body.contains("\"client_secret\":\"" + CLIENT_SECRET + "\""));
        }

        @Test
        @DisplayName("공백 구분 scope 문자열은 분리되어 JSON 배열로 전송된다")
        void refreshTokenWithScopeString() throws InterruptedException {
            enqueueToken(TOKEN_RESPONSE_JSON);

            oAuthApi.refreshToken("my-refresh-token", "  app:account_read   app:student_read ");

            JsonArray scope = takeBody().getAsJsonArray("scope");
            assertEquals(2, scope.size());
            assertEquals("app:account_read", scope.get(0).getAsString());
            assertEquals("app:student_read", scope.get(1).getAsString());
        }

        @Test
        @DisplayName("scope 목록은 중복과 빈 값을 제거해 JSON 배열로 전송된다")
        void refreshTokenWithScopeList() throws InterruptedException {
            enqueueToken(TOKEN_RESPONSE_JSON);

            oAuthApi.refreshToken("my-refresh-token", Arrays.asList("app:account_read", "", null, "app:account_read"));

            JsonArray scope = takeBody().getAsJsonArray("scope");
            assertEquals(1, scope.size());
            assertEquals("app:account_read", scope.get(0).getAsString());
        }

        @Test
        @DisplayName("빈 scope 목록이나 공백 문자열이면 scope 필드를 생략한다")
        void refreshTokenWithEmptyScopes() throws InterruptedException {
            enqueueToken(TOKEN_RESPONSE_JSON);
            enqueueToken(TOKEN_RESPONSE_JSON);

            oAuthApi.refreshToken("my-refresh-token", Collections.emptyList());
            assertFalse(takeBody().has("scope"));

            oAuthApi.refreshToken("my-refresh-token", "   ");
            assertFalse(takeBody().has("scope"));
        }

        @Test
        @DisplayName("scope를 지정하지 않으면 요청 본문에 scope가 없다")
        void refreshTokenWithoutScope() throws InterruptedException {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(TOKEN_RESPONSE_JSON));

            oAuthApi.refreshToken("my-refresh-token");

            RecordedRequest request = mockWebServer.takeRequest(3, TimeUnit.SECONDS);
            assertNotNull(request, "요청이 서버에 도달하지 않았습니다");
            String body = request.getBody().readUtf8();
            assertFalse(body.contains("\"scope\""));
        }
    }

    @Nested
    @DisplayName("getClientCredentialsToken()")
    class GetClientCredentialsToken {

        @Test
        @DisplayName("Client Credentials Grant로 Access Token을 발급받는다")
        void getTokenSucceeds() throws InterruptedException {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(CC_TOKEN_RESPONSE_JSON));

            TokenResponse response = oAuthApi.getClientCredentialsToken();

            assertEquals("cc-access-token", response.getAccessToken());
            assertNull(response.getRefreshToken());

            RecordedRequest request = mockWebServer.takeRequest(3, TimeUnit.SECONDS);
            assertNotNull(request, "요청이 서버에 도달하지 않았습니다");
            String body = request.getBody().readUtf8();
            assertTrue(body.contains("\"grant_type\":\"client_credentials\""));
            assertTrue(body.contains("\"client_id\":\"" + CLIENT_ID + "\""));
            assertTrue(body.contains("\"client_secret\":\"" + CLIENT_SECRET + "\""));
        }

        @Test
        @DisplayName("scope 문자열을 지정하면 JSON 배열로 전송된다")
        void getTokenWithScope() throws InterruptedException {
            enqueueToken(CC_TOKEN_RESPONSE_JSON);

            oAuthApi.getClientCredentialsToken("app:account_read");

            JsonArray scope = takeBody().getAsJsonArray("scope");
            assertEquals(1, scope.size());
            assertEquals("app:account_read", scope.get(0).getAsString());
        }

        @Test
        @DisplayName("scope 목록을 지정하면 JSON 배열로 전송된다")
        void getTokenWithScopeList() throws InterruptedException {
            enqueueToken(CC_TOKEN_RESPONSE_JSON);

            oAuthApi.getClientCredentialsToken(Arrays.asList("app:account_read", "app:club_read"));

            JsonArray scope = takeBody().getAsJsonArray("scope");
            assertEquals(2, scope.size());
            assertEquals("app:club_read", scope.get(1).getAsString());
        }

        @Test
        @DisplayName("scope를 지정하지 않으면 scope 필드를 생략한다")
        void getTokenWithoutScope() throws InterruptedException {
            enqueueToken(CC_TOKEN_RESPONSE_JSON);

            oAuthApi.getClientCredentialsToken();

            assertFalse(takeBody().has("scope"));
        }
    }

    @Nested
    @DisplayName("OIDC")
    class Oidc {

        private static final String DISCOVERY_JSON =
                "{\"issuer\":\"https://oauth.authorization.datagsm.kr\"," +
                        "\"authorization_endpoint\":\"https://oauth.authorization.datagsm.kr/v1/oauth/authorize\"," +
                        "\"token_endpoint\":\"https://oauth.authorization.datagsm.kr/v1/oauth/token\"," +
                        "\"jwks_uri\":\"https://oauth.authorization.datagsm.kr/v1/oauth/jwks\"," +
                        "\"userinfo_endpoint\":\"https://oauth.resource.datagsm.kr/userinfo\"," +
                        "\"response_types_supported\":[\"code\"]," +
                        "\"grant_types_supported\":[\"authorization_code\",\"refresh_token\",\"client_credentials\"]," +
                        "\"subject_types_supported\":[\"public\"]," +
                        "\"id_token_signing_alg_values_supported\":[\"RS256\"]," +
                        "\"code_challenge_methods_supported\":[\"S256\"]," +
                        "\"scopes_supported\":[\"openid\",\"app:account_read\"]," +
                        "\"token_endpoint_auth_methods_supported\":[\"client_secret_basic\",\"client_secret_post\",\"none\"]," +
                        "\"claims_supported\":[\"iss\",\"sub\",\"aud\",\"exp\",\"iat\",\"nonce\",\"email\"]}";

        @Test
        @DisplayName("openid scope 응답의 id_token을 역직렬화한다")
        void tokenResponseWithIdToken() {
            enqueueToken("{\"access_token\":\"at\",\"token_type\":\"Bearer\",\"expires_in\":3600," +
                    "\"refresh_token\":\"rt\",\"scope\":\"openid app:account_read\",\"id_token\":\"header.payload.sig\"}");

            TokenResponse response = oAuthApi.exchangeCodeForToken("code", REDIRECT_URI);

            assertEquals("header.payload.sig", response.getIdToken());
            assertEquals(Arrays.asList("openid", "app:account_read"), response.getScopes());
            assertFalse(response.toString().contains("header.payload.sig"));
        }

        @Test
        @DisplayName("Discovery 문서를 조회한다")
        @SuppressWarnings("deprecation")
        void getDiscoveryDocument() throws InterruptedException {
            enqueueToken(DISCOVERY_JSON);

            OidcDiscoveryDocument doc = oAuthApi.getDiscoveryDocument();

            assertEquals("https://oauth.authorization.datagsm.kr", doc.getIssuer());
            assertEquals("https://oauth.resource.datagsm.kr/userinfo", doc.getUserinfoEndpoint());
            assertNull(doc.getEndSessionEndpoint());
            assertTrue(doc.getTokenEndpointAuthMethodsSupported().contains("none"));
            assertEquals(Arrays.asList("S256"), doc.getCodeChallengeMethodsSupported());

            RecordedRequest request = mockWebServer.takeRequest(3, TimeUnit.SECONDS);
            assertNotNull(request);
            assertEquals("GET", request.getMethod());
            assertEquals("/.well-known/openid-configuration", request.getPath());
        }

        @Test
        @DisplayName("JWK Set을 조회하고 kid로 키를 찾는다")
        void getJwkSet() throws InterruptedException {
            enqueueToken("{\"keys\":[{\"kty\":\"RSA\",\"kid\":\"key-1\",\"n\":\"modulus\",\"e\":\"AQAB\"}]}");

            JsonWebKeySet jwks = oAuthApi.getJwkSet();

            assertEquals(1, jwks.getKeys().size());
            JsonWebKey key = jwks.findByKid("key-1");
            assertNotNull(key);
            assertEquals("RSA", key.getKty());
            assertEquals("AQAB", key.getE());
            assertNull(key.getAlg());
            assertNull(jwks.findByKid("other"));

            RecordedRequest request = mockWebServer.takeRequest(3, TimeUnit.SECONDS);
            assertNotNull(request);
            assertEquals("/v1/oauth/jwks", request.getPath());
        }

        @Test
        @DisplayName("로그아웃 엔드포인트 URL을 반환한다")
        void logoutEndpoint() {
            assertEquals("http://localhost:" + mockWebServer.getPort() + "/v1/oauth/logout",
                    oAuthApi.getLogoutEndpoint());
        }
    }

    @Nested
    @DisplayName("public client (client_secret 없음)")
    class PublicClient {

        private OAuthApiImpl publicApi;

        @BeforeEach
        void setUpPublic() {
            publicApi = new OAuthApiImpl(new OkHttpClientImpl(), CLIENT_ID, null,
                    "http://localhost:" + mockWebServer.getPort());
        }

        @Test
        @DisplayName("PKCE 교환은 client_secret 없이 code_verifier로 요청한다")
        void exchangeWithPkce() throws InterruptedException {
            enqueueToken(TOKEN_RESPONSE_JSON);

            publicApi.exchangeCodeForToken("code", REDIRECT_URI, "verifier");

            JsonObject body = takeBody();
            assertEquals("verifier", body.get("code_verifier").getAsString());
            assertEquals(CLIENT_ID, body.get("client_id").getAsString());
            assertFalse(body.has("client_secret"));
        }

        @Test
        @DisplayName("code_verifier 없이 교환하면 요청 전에 IllegalStateException이 발생한다")
        void exchangeWithoutPkceThrows() {
            assertThrows(IllegalStateException.class,
                    () -> publicApi.exchangeCodeForToken("code", REDIRECT_URI));
            assertEquals(0, mockWebServer.getRequestCount());
        }

        @Test
        @DisplayName("refresh 요청에서 client_secret을 생략한다")
        void refreshWithoutSecret() throws InterruptedException {
            enqueueToken(TOKEN_RESPONSE_JSON);

            publicApi.refreshToken("rt");

            JsonObject body = takeBody();
            assertEquals("refresh_token", body.get("grant_type").getAsString());
            assertEquals(CLIENT_ID, body.get("client_id").getAsString());
            assertFalse(body.has("client_secret"));
        }

        @Test
        @DisplayName("client_credentials grant는 요청 전에 IllegalStateException이 발생한다")
        void clientCredentialsThrows() {
            assertThrows(IllegalStateException.class, () -> publicApi.getClientCredentialsToken());
            assertEquals(0, mockWebServer.getRequestCount());
        }
    }

    private void enqueueToken(String json) {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(json));
    }

    private JsonObject takeBody() throws InterruptedException {
        RecordedRequest request = mockWebServer.takeRequest(3, TimeUnit.SECONDS);
        assertNotNull(request, "요청이 서버에 도달하지 않았습니다");
        return JsonParser.parseString(request.getBody().readUtf8()).getAsJsonObject();
    }
}
