package team.themoment.datagsm.sdk.oauth.client;

import com.google.gson.reflect.TypeToken;
import team.themoment.datagsm.sdk.oauth.http.HttpClient;
import team.themoment.datagsm.sdk.oauth.http.JsonUtil;
import team.themoment.datagsm.sdk.oauth.model.CommonApiResponse;
import team.themoment.datagsm.sdk.oauth.model.TokenResponse;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth API 구현
 */
public class OAuthApiImpl implements OAuthApi {
    private final HttpClient httpClient;
    private final String clientSecret;
    private final String baseUrl;

    public OAuthApiImpl(HttpClient httpClient, String clientSecret, String baseUrl) {
        this.httpClient = httpClient;
        this.clientSecret = clientSecret;
        this.baseUrl = baseUrl;
    }

    @Override
    public TokenResponse exchangeToken(String code) {
        Map<String, String> headers = createJsonHeaders();

        Map<String, Object> body = new HashMap<>();
        body.put("code", code);
        body.put("clientSecret", clientSecret);

        String responseBody = httpClient.post(
                baseUrl + "/v1/oauth/token",
                headers,
                JsonUtil.toJson(body)
        );

        Type type = new TypeToken<CommonApiResponse<TokenResponse>>(){}.getType();
        CommonApiResponse<TokenResponse> apiResponse = JsonUtil.fromJson(responseBody, type);
        return apiResponse.getData();
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        Map<String, String> headers = createJsonHeaders();

        Map<String, Object> body = new HashMap<>();
        body.put("refreshToken", refreshToken);

        String responseBody = httpClient.put(
                baseUrl + "/v1/oauth/refresh",
                headers,
                JsonUtil.toJson(body)
        );

        Type type = new TypeToken<CommonApiResponse<TokenResponse>>(){}.getType();
        CommonApiResponse<TokenResponse> apiResponse = JsonUtil.fromJson(responseBody, type);
        return apiResponse.getData();
    }

    private Map<String, String> createJsonHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
    }
}
