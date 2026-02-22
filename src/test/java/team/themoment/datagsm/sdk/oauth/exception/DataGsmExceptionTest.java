package team.themoment.datagsm.sdk.oauth.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DataGsmException")
class DataGsmExceptionTest {

    @Test
    @DisplayName("메시지만으로 생성하면 statusCode는 NO_STATUS_CODE(-1)이다")
    void noStatusCodeWhenCreatedWithMessageOnly() {
        DataGsmException ex = new DataGsmException("error");

        assertEquals(DataGsmException.NO_STATUS_CODE, ex.getStatusCode());
        assertFalse(ex.hasStatusCode());
    }

    @Test
    @DisplayName("statusCode를 지정하면 hasStatusCode()가 true를 반환한다")
    void hasStatusCodeWhenSet() {
        DataGsmException ex = new DataGsmException("error", 400);

        assertEquals(400, ex.getStatusCode());
        assertTrue(ex.hasStatusCode());
    }

    @Test
    @DisplayName("cause와 함께 생성하면 statusCode는 NO_STATUS_CODE(-1)이다")
    void noStatusCodeWhenCreatedWithCause() {
        DataGsmException ex = new DataGsmException("error", new RuntimeException("cause"));

        assertEquals(DataGsmException.NO_STATUS_CODE, ex.getStatusCode());
        assertFalse(ex.hasStatusCode());
    }

    @Test
    @DisplayName("statusCode와 cause 모두 지정할 수 있다")
    void statusCodeAndCauseBothSet() {
        RuntimeException cause = new RuntimeException("cause");
        DataGsmException ex = new DataGsmException("error", 503, cause);

        assertEquals(503, ex.getStatusCode());
        assertTrue(ex.hasStatusCode());
        assertEquals(cause, ex.getCause());
    }

    @Test
    @DisplayName("NO_STATUS_CODE 상수는 -1이다")
    void noStatusCodeConstantIsMinusOne() {
        assertEquals(-1, DataGsmException.NO_STATUS_CODE);
    }

    @Test
    @DisplayName("하위 예외 클래스들은 DataGsmException을 상속한다")
    void subclassesExtendDataGsmException() {
        assertInstanceOf(DataGsmException.class, new BadRequestException("bad"));
        assertInstanceOf(DataGsmException.class, new UnauthorizedException("unauth"));
        assertInstanceOf(DataGsmException.class, new ForbiddenException("forbidden"));
        assertInstanceOf(DataGsmException.class, new NotFoundException("not found"));
        assertInstanceOf(DataGsmException.class, new RateLimitException("rate limit"));
        assertInstanceOf(DataGsmException.class, new ServerErrorException("server error"));
    }
}