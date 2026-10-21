package team.themoment.datagsm.sdk.oauth.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.themoment.datagsm.sdk.oauth.exception.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ErrorResponseParser")
class ErrorResponseParserTest {

    @Test
    @DisplayName("OAuth 에러 형식의 error, error_description을 파싱한다")
    void parsesOAuthError() {
        String body = "{\"error\":\"invalid_grant\",\"error_description\":\"Refresh Token이 유효하지 않거나 만료되었습니다.\"}";

        DataGsmException ex = ErrorResponseParser.toException(400, body);

        assertInstanceOf(BadRequestException.class, ex);
        assertEquals(400, ex.getStatusCode());
        assertEquals("invalid_grant", ex.getErrorCode());
        assertEquals("Refresh Token이 유효하지 않거나 만료되었습니다.", ex.getErrorDescription());
        assertEquals("[invalid_grant] Refresh Token이 유효하지 않거나 만료되었습니다.", ex.getMessage());
        assertEquals(body, ex.getResponseBody());
    }

    @Test
    @DisplayName("error_description이 없는 OAuth 에러는 error만 담는다")
    void parsesOAuthErrorWithoutDescription() {
        DataGsmException ex = ErrorResponseParser.toException(401, "{\"error\":\"invalid_client\"}");

        assertInstanceOf(UnauthorizedException.class, ex);
        assertEquals("invalid_client", ex.getErrorCode());
        assertNull(ex.getErrorDescription());
        assertEquals("invalid_client", ex.getMessage());
    }

    @Test
    @DisplayName("CommonApiResponse 형식의 status, message를 파싱한다")
    void parsesCommonApiResponse() {
        String body = "{\"status\":\"TOO_MANY_REQUESTS\",\"code\":429,\"message\":\"요청 한도를 초과했습니다.\"}";

        DataGsmException ex = ErrorResponseParser.toException(429, body);

        assertInstanceOf(RateLimitException.class, ex);
        assertEquals("TOO_MANY_REQUESTS", ex.getErrorCode());
        assertEquals("요청 한도를 초과했습니다.", ex.getErrorDescription());
        assertEquals("[TOO_MANY_REQUESTS] 요청 한도를 초과했습니다.", ex.getMessage());
    }

    @Test
    @DisplayName("JSON이 아닌 본문은 그대로 메시지로 사용하고 errorCode는 null이다")
    void plainTextBody() {
        DataGsmException ex = ErrorResponseParser.toException(403, "Forbidden page");

        assertInstanceOf(ForbiddenException.class, ex);
        assertNull(ex.getErrorCode());
        assertNull(ex.getErrorDescription());
        assertEquals("Forbidden page", ex.getMessage());
    }

    @Test
    @DisplayName("알 수 없는 JSON 구조는 원본 본문을 메시지로 사용한다")
    void unknownJsonShape() {
        DataGsmException ex = ErrorResponseParser.toException(404, "{\"foo\":1}");

        assertInstanceOf(NotFoundException.class, ex);
        assertNull(ex.getErrorCode());
        assertEquals("{\"foo\":1}", ex.getMessage());
    }

    @Test
    @DisplayName("본문이 없으면 기본 메시지를 사용하고 responseBody는 null이다")
    void emptyBody() {
        DataGsmException ex = ErrorResponseParser.toException(500, "");

        assertInstanceOf(ServerErrorException.class, ex);
        assertEquals("HTTP 500 error", ex.getMessage());
        assertNull(ex.getResponseBody());
    }

    @Test
    @DisplayName("5xx 응답은 실제 상태 코드를 유지한다")
    void keepsActualServerErrorStatus() {
        DataGsmException ex = ErrorResponseParser.toException(503, null);

        assertInstanceOf(ServerErrorException.class, ex);
        assertEquals(503, ex.getStatusCode());
    }

    @Test
    @DisplayName("매핑되지 않은 상태 코드는 DataGsmException으로 변환한다")
    void unmappedStatus() {
        DataGsmException ex = ErrorResponseParser.toException(422,
                "{\"status\":\"UNPROCESSABLE_ENTITY\",\"code\":422,\"message\":\"처리할 수 없습니다.\"}");

        assertEquals(DataGsmException.class, ex.getClass());
        assertEquals(422, ex.getStatusCode());
        assertEquals("UNPROCESSABLE_ENTITY", ex.getErrorCode());
    }
}
