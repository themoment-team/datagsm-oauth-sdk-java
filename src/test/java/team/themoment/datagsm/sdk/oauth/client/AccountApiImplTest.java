package team.themoment.datagsm.sdk.oauth.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import team.themoment.datagsm.sdk.oauth.exception.UnauthorizedException;
import team.themoment.datagsm.sdk.oauth.http.OkHttpClientImpl;
import team.themoment.datagsm.sdk.oauth.model.AccountObjectType;
import team.themoment.datagsm.sdk.oauth.model.AccountStatus;
import team.themoment.datagsm.sdk.oauth.model.TeacherDepartment;
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
                    "\"isStudent\":false,\"student\":null," +
                    "\"status\":\"ACTIVE\",\"objectType\":null,\"teacher\":null}";

    private static final String STUDENT_RESPONSE =
            "{\"id\":2,\"email\":\"student@gsm.hs.kr\",\"role\":\"USER\",\"isStudent\":true," +
                    "\"student\":{\"id\":10,\"name\":\"홍길동\",\"sex\":\"MAN\",\"email\":\"student@gsm.hs.kr\"," +
                    "\"grade\":2,\"classNum\":3,\"number\":15,\"studentNumber\":2315," +
                    "\"major\":\"SW_DEVELOPMENT\",\"role\":\"GENERAL_STUDENT\"," +
                    "\"dormitoryFloor\":3,\"dormitoryRoom\":302,\"isLeaveSchool\":false," +
                    "\"majorClub\":null,\"autonomousClub\":null}," +
                    "\"status\":\"ACTIVE\",\"objectType\":\"STUDENT\",\"teacher\":null}";

    private static final String TEACHER_RESPONSE =
            "{\"id\":3,\"email\":\"teacher@gsm.hs.kr\",\"role\":\"USER\"," +
                    "\"isStudent\":false,\"student\":null," +
                    "\"status\":\"ACTIVE\",\"objectType\":\"TEACHER\"," +
                    "\"teacher\":{\"id\":20,\"name\":\"김선생\",\"email\":\"teacher@gsm.hs.kr\"," +
                    "\"department\":\"MEISTER\",\"description\":\"마이스터부 담당\"}}";

    private static final String PENDING_TEACHER_RESPONSE =
            "{\"id\":4,\"email\":\"pending-teacher@gsm.hs.kr\",\"role\":\"USER\"," +
                    "\"isStudent\":false,\"student\":null," +
                    "\"status\":\"PENDING\",\"objectType\":\"TEACHER\"," +
                    "\"teacher\":{\"id\":21,\"name\":\"이선생\",\"email\":\"pending-teacher@gsm.hs.kr\"," +
                    "\"department\":\"ACADEMIC_AFFAIRS\",\"description\":null}}";

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
            assertEquals(AccountStatus.ACTIVE, userInfo.getStatus());
            assertNull(userInfo.getObjectType());
            assertNull(userInfo.getTeacher());

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
            assertEquals(AccountStatus.ACTIVE, userInfo.getStatus());
            assertEquals(AccountObjectType.STUDENT, userInfo.getObjectType());
            assertNull(userInfo.getTeacher());
        }

        @Test
        @DisplayName("Access Token으로 사용자 정보를 조회한다 (선생님인 경우)")
        void getUserInfoTeacher() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(TEACHER_RESPONSE));

            UserInfo userInfo = accountApi.getUserInfo("valid-access-token");

            assertEquals(AccountStatus.ACTIVE, userInfo.getStatus());
            assertEquals(AccountObjectType.TEACHER, userInfo.getObjectType());
            assertFalse(userInfo.isStudent());
            assertNull(userInfo.getStudent());
            assertNotNull(userInfo.getTeacher());
            assertEquals(20L, userInfo.getTeacher().getId());
            assertEquals("김선생", userInfo.getTeacher().getName());
            assertEquals(TeacherDepartment.MEISTER, userInfo.getTeacher().getDepartment());
            assertEquals("마이스터부 담당", userInfo.getTeacher().getDescription());
        }

        @Test
        @DisplayName("Access Token으로 사용자 정보를 조회한다 (승인 대기중인 선생님인 경우)")
        void getUserInfoPendingTeacher() {
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(PENDING_TEACHER_RESPONSE));

            UserInfo userInfo = accountApi.getUserInfo("valid-access-token");

            assertEquals(AccountStatus.PENDING, userInfo.getStatus());
            assertEquals(AccountObjectType.TEACHER, userInfo.getObjectType());
            assertNotNull(userInfo.getTeacher());
            assertEquals(TeacherDepartment.ACADEMIC_AFFAIRS, userInfo.getTeacher().getDepartment());
            assertNull(userInfo.getTeacher().getDescription());
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