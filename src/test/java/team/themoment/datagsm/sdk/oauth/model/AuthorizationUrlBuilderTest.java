package team.themoment.datagsm.sdk.oauth.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthorizationUrlBuilder")
class AuthorizationUrlBuilderTest {

    private static final String ENDPOINT = "https://oauth.data.hellogsm.kr/v1/oauth/authorize";
    private static final String CLIENT_ID = "test-client";
    private static final String REDIRECT_URI = "https://myapp.com/callback";

    @Nested
    @DisplayName("build()")
    class Build {

        @Test
        @DisplayName("필수 파라미터만으로 URL을 생성한다")
        void buildWithRequiredParams() {
            AuthorizationUrlBuilder builder = new AuthorizationUrlBuilder(ENDPOINT, CLIENT_ID)
                    .redirectUri(REDIRECT_URI);

            String url = builder.build();

            assertTrue(url.startsWith(ENDPOINT));
            assertTrue(url.contains("response_type=code"));
            assertTrue(url.contains("client_id=" + CLIENT_ID));
            assertTrue(url.contains("redirect_uri="));
        }

        @Test
        @DisplayName("state 파라미터가 URL에 포함된다")
        void buildWithState() {
            String state = "random-csrf-token";

            String url = new AuthorizationUrlBuilder(ENDPOINT, CLIENT_ID)
                    .redirectUri(REDIRECT_URI)
                    .state(state)
                    .build();

            assertTrue(url.contains("state=" + state));
        }

        @Test
        @DisplayName("scope 파라미터가 URL에 포함된다")
        void buildWithScope() {
            String url = new AuthorizationUrlBuilder(ENDPOINT, CLIENT_ID)
                    .redirectUri(REDIRECT_URI)
                    .scope("read write")
                    .build();

            // "read write"는 URL 인코딩되어 "read+write" 또는 "read%20write"로 포함됨
            assertTrue(url.contains("scope="));
            String decoded = URLDecoder.decode(url, StandardCharsets.UTF_8);
            assertTrue(decoded.contains("scope=read write"));
        }

        @Test
        @DisplayName("redirectUri가 없으면 IllegalStateException이 발생한다")
        void buildWithoutRedirectUri() {
            AuthorizationUrlBuilder builder = new AuthorizationUrlBuilder(ENDPOINT, CLIENT_ID);

            assertThrows(IllegalStateException.class, builder::build);
        }
    }

    @Nested
    @DisplayName("enablePkce()")
    class EnablePkce {

        @Test
        @DisplayName("PKCE 자동 생성 시 code_challenge와 code_challenge_method가 URL에 포함된다")
        void enablePkceAddsParams() {
            AuthorizationUrlBuilder builder = new AuthorizationUrlBuilder(ENDPOINT, CLIENT_ID)
                    .redirectUri(REDIRECT_URI)
                    .enablePkce();

            String url = builder.build();

            assertTrue(url.contains("code_challenge="));
            assertTrue(url.contains("code_challenge_method=S256"));
        }

        @Test
        @DisplayName("PKCE 활성화 후 getCodeVerifier()가 null이 아닌 값을 반환한다")
        void codeVerifierIsNotNull() {
            AuthorizationUrlBuilder builder = new AuthorizationUrlBuilder(ENDPOINT, CLIENT_ID)
                    .redirectUri(REDIRECT_URI)
                    .enablePkce();

            assertNotNull(builder.getCodeVerifier());
            assertFalse(builder.getCodeVerifier().isEmpty());
        }

        @Test
        @DisplayName("PKCE 미활성화 시 getCodeVerifier()는 null을 반환한다")
        void codeVerifierIsNullWithoutPkce() {
            AuthorizationUrlBuilder builder = new AuthorizationUrlBuilder(ENDPOINT, CLIENT_ID)
                    .redirectUri(REDIRECT_URI);

            assertNull(builder.getCodeVerifier());
        }

        @Test
        @DisplayName("수동 Code Verifier와 S256 메소드로 PKCE를 활성화한다")
        void enablePkceWithManualVerifierS256() {
            String verifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";

            AuthorizationUrlBuilder builder = new AuthorizationUrlBuilder(ENDPOINT, CLIENT_ID)
                    .redirectUri(REDIRECT_URI)
                    .enablePkce(verifier, "S256");

            String url = builder.build();
            assertTrue(url.contains("code_challenge="));
            assertTrue(url.contains("code_challenge_method=S256"));
            assertEquals(verifier, builder.getCodeVerifier());
        }

        @Test
        @DisplayName("수동 Code Verifier와 plain 메소드로 PKCE를 활성화한다")
        void enablePkceWithManualVerifierPlain() {
            String verifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";

            AuthorizationUrlBuilder builder = new AuthorizationUrlBuilder(ENDPOINT, CLIENT_ID)
                    .redirectUri(REDIRECT_URI)
                    .enablePkce(verifier, "plain");

            String url = builder.build();
            assertTrue(url.contains("code_challenge_method=plain"));
        }

        @Test
        @DisplayName("지원하지 않는 code_challenge_method는 IllegalArgumentException이 발생한다")
        void invalidMethodThrowsException() {
            AuthorizationUrlBuilder builder = new AuthorizationUrlBuilder(ENDPOINT, CLIENT_ID)
                    .redirectUri(REDIRECT_URI);

            assertThrows(IllegalArgumentException.class,
                    () -> builder.enablePkce("some-verifier", "RS256"));
        }

        @Test
        @DisplayName("호출할 때마다 다른 Code Verifier가 생성된다")
        void codeVerifierIsRandom() {
            AuthorizationUrlBuilder builder1 = new AuthorizationUrlBuilder(ENDPOINT, CLIENT_ID)
                    .redirectUri(REDIRECT_URI)
                    .enablePkce();

            AuthorizationUrlBuilder builder2 = new AuthorizationUrlBuilder(ENDPOINT, CLIENT_ID)
                    .redirectUri(REDIRECT_URI)
                    .enablePkce();

            assertNotEquals(builder1.getCodeVerifier(), builder2.getCodeVerifier());
        }
    }
}