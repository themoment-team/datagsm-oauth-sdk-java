package team.themoment.datagsm.sdk.oauth.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import team.themoment.datagsm.sdk.oauth.exception.BadRequestException;
import team.themoment.datagsm.sdk.oauth.exception.UnauthorizedException;
import team.themoment.datagsm.sdk.oauth.http.OkHttpClientImpl;
import team.themoment.datagsm.sdk.oauth.model.*;

import java.io.IOException;
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
            "{\"status\":\"OK\",\"code\":200,\"message\":\"success\"," +
                    "\"data\":{\"access_token\":\"test-access-token\",\"token_type\":\"Bearer\"," +
                    "\"expires_in\":3600,\"refresh_token\":\"test-refresh-token\",\"scope\":\"read\"}}";

    private static final String CC_TOKEN_RESPONSE_JSON =
            "{\"status\":\"OK\",\"code\":200,\"message\":\"success\"," +
                    "\"data\":{\"access_token\":\"cc-access-token\",\"token_type\":\"Bearer\"," +
                    "\"expires_in\":3600,\"refresh_token\":null,\"scope\":\"read\"}}";

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
        @DisplayName("scope를 지정하면 요청 본문에 포함된다")
        void refreshTokenWithScope() throws InterruptedException {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(TOKEN_RESPONSE_JSON));

            oAuthApi.refreshToken("my-refresh-token", "read");

            RecordedRequest request = mockWebServer.takeRequest(3, TimeUnit.SECONDS);
            assertNotNull(request, "요청이 서버에 도달하지 않았습니다");
            String body = request.getBody().readUtf8();
            assertTrue(body.contains("\"scope\":\"read\""));
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
        @DisplayName("scope를 지정하면 요청 본문에 포함된다")
        void getTokenWithScope() throws InterruptedException {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(CC_TOKEN_RESPONSE_JSON));

            oAuthApi.getClientCredentialsToken("api.read");

            RecordedRequest request = mockWebServer.takeRequest(3, TimeUnit.SECONDS);
            assertNotNull(request, "요청이 서버에 도달하지 않았습니다");
            String body = request.getBody().readUtf8();
            assertTrue(body.contains("\"scope\":\"api.read\""));
        }
    }
}