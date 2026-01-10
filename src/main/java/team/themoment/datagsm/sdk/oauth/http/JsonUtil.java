package team.themoment.datagsm.sdk.oauth.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import team.themoment.datagsm.sdk.oauth.exception.DataGsmException;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * JSON 직렬화/역직렬화 유틸리티
 */
public class JsonUtil {
    private static final Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder();

        // LocalDate 처리
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        JsonDeserializer<LocalDate> dateDeserializer = (json, typeOfT, context) ->
                LocalDate.parse(json.getAsString(), dateFormatter);

        builder.registerTypeAdapter(LocalDate.class, dateDeserializer);
        builder.registerTypeAdapter(LocalDate.class, (com.google.gson.JsonSerializer<LocalDate>)
                (src, typeOfSrc, context) -> context.serialize(src.format(dateFormatter)));

        GSON = builder.create();
    }

    /**
     * JSON 문자열을 객체로 변환
     *
     * @param json JSON 문자열
     * @param clazz 대상 클래스
     * @param <T> 타입
     * @return 변환된 객체
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            throw new DataGsmException("Failed to deserialize JSON: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 문자열을 객체로 변환 (제네릭 타입)
     *
     * @param json JSON 문자열
     * @param type 대상 타입
     * @param <T> 타입
     * @return 변환된 객체
     */
    public static <T> T fromJson(String json, Type type) {
        try {
            return GSON.fromJson(json, type);
        } catch (Exception e) {
            throw new DataGsmException("Failed to deserialize JSON: " + e.getMessage(), e);
        }
    }

    /**
     * 객체를 JSON 문자열로 변환
     *
     * @param obj 변환할 객체
     * @return JSON 문자열
     */
    public static String toJson(Object obj) {
        try {
            return GSON.toJson(obj);
        } catch (Exception e) {
            throw new DataGsmException("Failed to serialize JSON: " + e.getMessage(), e);
        }
    }
}
