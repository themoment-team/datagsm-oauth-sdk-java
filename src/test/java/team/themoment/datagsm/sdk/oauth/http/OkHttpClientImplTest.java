package team.themoment.datagsm.sdk.oauth.http;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import team.themoment.datagsm.sdk.oauth.exception.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OkHttpClientImpl")
class OkHttpClientImplTest {

    private MockWebServer mockWebServer;
    private OkHttpClientImpl httpClient;
    private String baseUrl;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        httpClient = new OkHttpClientImpl();
        baseUrl = "http://localhost:" + mockWebServer.getPort();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("get()")
    class Get {

        @Test
        @DisplayName("GET 요청이 성공하면 응답 본문을 반환한다")
        void getSucceeds() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("{\"key\":\"value\"}"));

            String result = httpClient.get(baseUrl + "/test", null, null);

            assertEquals("{\"key\":\"value\"}", result);
        }

        @Test
        @DisplayName("쿼리 파라미터가 URL에 추가된다")
        void getWithQueryParams() throws InterruptedException {
            mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(""));

            httpClient.get(baseUrl + "/test", null, Map.of("key", "value"));

            var request = mockWebServer.takeRequest(3, TimeUnit.SECONDS);
            assertTrue(request.getPath().contains("key=value"));
        }

        @Test
        @DisplayName("헤더가 요청에 포함된다")
        void getWithHeaders() throws InterruptedException {
            mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(""));

            httpClient.get(baseUrl + "/test", Map.of("Authorization", "Bearer token"), null);

            var request = mockWebServer.takeRequest(3, TimeUnit.SECONDS);
            assertEquals("Bearer token", request.getHeader("Authorization"));
        }

        @Test
        @DisplayName("잘못된 URL이면 DataGsmException이 발생한다")
        void getWithInvalidUrl() {
            assertThrows(DataGsmException.class,
                    () -> httpClient.get("not-a-valid-url", null, null));
        }
    }

    @Nested
    @DisplayName("post()")
    class Post {

        @Test
        @DisplayName("POST 요청이 성공하면 응답 본문을 반환한다")
        void postSucceeds() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setBody("{\"result\":\"ok\"}"));

            String result = httpClient.post(baseUrl + "/test", null, "{\"data\":\"test\"}");

            assertEquals("{\"result\":\"ok\"}", result);
        }
    }

    @Nested
    @DisplayName("에러 응답 처리")
    class ErrorHandling {

        @Test
        @DisplayName("400 응답은 BadRequestException을 발생시킨다")
        void handles400() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("Bad Request"));
            assertThrows(BadRequestException.class,
                    () -> httpClient.get(baseUrl + "/test", null, null));
        }

        @Test
        @DisplayName("401 응답은 UnauthorizedException을 발생시킨다")
        void handles401() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(401).setBody("Unauthorized"));
            assertThrows(UnauthorizedException.class,
                    () -> httpClient.get(baseUrl + "/test", null, null));
        }

        @Test
        @DisplayName("403 응답은 ForbiddenException을 발생시킨다")
        void handles403() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(403).setBody("Forbidden"));
            assertThrows(ForbiddenException.class,
                    () -> httpClient.get(baseUrl + "/test", null, null));
        }

        @Test
        @DisplayName("404 응답은 NotFoundException을 발생시킨다")
        void handles404() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(404).setBody("Not Found"));
            assertThrows(NotFoundException.class,
                    () -> httpClient.get(baseUrl + "/test", null, null));
        }

        @Test
        @DisplayName("429 응답은 RateLimitException을 발생시킨다")
        void handles429() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(429).setBody("Too Many Requests"));
            assertThrows(RateLimitException.class,
                    () -> httpClient.get(baseUrl + "/test", null, null));
        }

        @Test
        @DisplayName("500 응답은 ServerErrorException을 발생시킨다")
        void handles500() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));
            assertThrows(ServerErrorException.class,
                    () -> httpClient.get(baseUrl + "/test", null, null));
        }

        @Test
        @DisplayName("502 응답은 ServerErrorException을 발생시킨다")
        void handles502() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(502).setBody("Bad Gateway"));
            assertThrows(ServerErrorException.class,
                    () -> httpClient.get(baseUrl + "/test", null, null));
        }

        @Test
        @DisplayName("응답 본문이 없으면 기본 에러 메시지를 사용한다")
        void defaultErrorMessageWhenBodyIsEmpty() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody(""));

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> httpClient.get(baseUrl + "/test", null, null));

            assertEquals("HTTP 400 error", ex.getMessage());
        }

        @Test
        @DisplayName("응답 본문이 있으면 본문을 에러 메시지로 사용한다")
        void responseBodyUsedAsErrorMessage() {
            mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("invalid_grant"));

            BadRequestException ex = assertThrows(BadRequestException.class,
                    () -> httpClient.get(baseUrl + "/test", null, null));

            assertEquals("invalid_grant", ex.getMessage());
        }
    }
}