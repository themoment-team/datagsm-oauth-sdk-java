package team.themoment.datagsm.sdk.oauth.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import team.themoment.datagsm.sdk.oauth.exception.*;

/**
 * HTTP 에러 응답을 {@link DataGsmException} 계열 예외로 변환합니다.
 * <p>
 * 서버는 OAuth 2.0 형식({@code error}, {@code error_description})과
 * CommonApiResponse 형식({@code status}, {@code code}, {@code message})을 모두 사용하므로 둘 다 해석합니다.
 * 커스텀 {@link HttpClient} 구현에서도 재사용할 수 있습니다.
 */
public final class ErrorResponseParser {

    private ErrorResponseParser() {
    }

    /**
     * 상태 코드와 응답 본문으로 예외를 생성합니다.
     *
     * @param statusCode   HTTP 상태 코드
     * @param responseBody 응답 본문 (null 허용)
     * @return 상태 코드에 맞는 예외
     */
    public static DataGsmException toException(int statusCode, String responseBody) {
        String body = responseBody != null && !responseBody.isEmpty() ? responseBody : null;
        ParsedError parsed = parse(body);
        String errorCode = parsed != null ? parsed.errorCode : null;
        String description = parsed != null ? parsed.description : null;
        String message = buildMessage(statusCode, body, errorCode, description);

        switch (statusCode) {
            case 400:
                return new BadRequestException(message, errorCode, description, body);
            case 401:
                return new UnauthorizedException(message, errorCode, description, body);
            case 403:
                return new ForbiddenException(message, errorCode, description, body);
            case 404:
                return new NotFoundException(message, errorCode, description, body);
            case 429:
                return new RateLimitException(message, errorCode, description, body);
            case 500:
            case 502:
            case 503:
            case 504:
                return new ServerErrorException(message, statusCode, errorCode, description, body);
            default:
                return new DataGsmException(message, statusCode, errorCode, description, body);
        }
    }

    private static String buildMessage(int statusCode, String body, String errorCode, String description) {
        if (errorCode != null && description != null) {
            return "[" + errorCode + "] " + description;
        }
        if (description != null) {
            return description;
        }
        if (errorCode != null) {
            return errorCode;
        }
        return body != null ? body : "HTTP " + statusCode + " error";
    }

    private static ParsedError parse(String body) {
        if (body == null) {
            return null;
        }
        JsonObject json;
        try {
            JsonElement element = JsonParser.parseString(body);
            if (!element.isJsonObject()) {
                return null;
            }
            json = element.getAsJsonObject();
        } catch (JsonParseException | IllegalStateException e) {
            return null;
        }

        String oauthError = stringOrNull(json, "error");
        if (oauthError != null) {
            return new ParsedError(oauthError, stringOrNull(json, "error_description"));
        }

        String message = stringOrNull(json, "message");
        String status = stringOrNull(json, "status");
        if (message != null || status != null) {
            return new ParsedError(status, message);
        }
        return null;
    }

    private static String stringOrNull(JsonObject json, String key) {
        JsonElement value = json.get(key);
        if (value == null || !value.isJsonPrimitive()) {
            return null;
        }
        String text = value.getAsString();
        return text.isEmpty() ? null : text;
    }

    private static final class ParsedError {
        private final String errorCode;
        private final String description;

        private ParsedError(String errorCode, String description) {
            this.errorCode = errorCode;
            this.description = description;
        }
    }
}
