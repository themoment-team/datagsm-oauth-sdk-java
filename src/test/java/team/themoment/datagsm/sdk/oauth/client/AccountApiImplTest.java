package team.themoment.datagsm.sdk.oauth.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import team.themoment.datagsm.sdk.oauth.exception.UnauthorizedException;
import team.themoment.datagsm.sdk.oauth.http.OkHttpClientImpl;
import team.themoment.datagsm.sdk.oauth.model.UserInfo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccountApiImpl")
class AccountApiImplTest {

    private MockWebServer mockWebServer;
    private AccountApiImpl accountApi;

    private static final String NON_STUDENT_RESPONSE =
            "{\"id\":1,\"email\":\"test@gsm.hs.kr\",\"role\":\"USER\"," +
                    "\"isStudent\":false,\"student\":null}";

    private static final String STUDENT_RESPONSE =
            "{\"id\":2,\"email\":\"student@gsm.hs.kr\",\"role\":\"USER\",\"isStudent\":true," +
                    "\"student\":{\"id\":10,\"name\":\"홍길동\",\"sex\":\"MAN\",\"email\":\"student@gsm.hs.kr\"," +
                    "\"grade\":2,\"classNum\":3,\"number\":15,\"studentNumber\":2315," +
                    "\"major\":\"SW_DEVELOPMENT\",\"role\":\"GENERAL_STUDENT\"," +
                    "\"dormitoryFloor\":3,\"dormitoryRoom\":302,\"isLeaveSchool\":false," +
                    "\"majorClub\":null,\"autonomousClub\":null}}";

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = "http://localhost:" + mockWebServer.getPort();
        accountApi = new AccountApiImpl(new OkHttpClientImpl(), baseUrl);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("getUserInfo()")
    class GetUserInfo {

        @Test
        @DisplayName("Access Token으로 사용자 정보를 조회한다 (학생 아닌 경우)")
        void getUserInfoNonStudent() throws InterruptedException {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(NON_STUDENT_RESPONSE));

            UserInfo userInfo = accountApi.getUserInfo("valid-access-token");

            assertEquals(1L, userInfo.getId());
            assertEquals("test@gsm.hs.kr", userInfo.getEmail());
            assertFalse(userInfo.isStudent());
            assertNull(userInfo.getStudent());

            RecordedRequest request = mockWebServer.takeRequest(3, TimeUnit.SECONDS);
            assertNotNull(request, "요청이 서버에 도달하지 않았습니다");
            assertEquals("Bearer valid-access-token", request.getHeader("Authorization"));
            assertEquals("/userinfo", request.getPath());
        }

        @Test
        @DisplayName("Access Token으로 사용자 정보를 조회한다 (학생인 경우)")
        void getUserInfoStudent() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(STUDENT_RESPONSE));

            UserInfo userInfo = accountApi.getUserInfo("valid-access-token");

            assertTrue(userInfo.isStudent());
            assertNotNull(userInfo.getStudent());
            assertEquals("홍길동", userInfo.getStudent().getName());
            assertEquals(2, userInfo.getStudent().getGrade());
            assertEquals(2315, userInfo.getStudent().getStudentNumber());
            assertFalse(userInfo.getStudent().isLeaveSchool());
        }

        @Test
        @DisplayName("만료된 Access Token으로 요청 시 UnauthorizedException이 발생한다")
        void throwsUnauthorizedOnExpiredToken() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(401)
                    .setBody("Unauthorized"));

            assertThrows(UnauthorizedException.class,
                    () -> accountApi.getUserInfo("expired-token"));
        }
    }
}