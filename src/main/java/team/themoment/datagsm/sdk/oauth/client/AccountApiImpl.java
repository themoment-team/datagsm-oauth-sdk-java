package team.themoment.datagsm.sdk.oauth.client;

import com.google.gson.reflect.TypeToken;
import team.themoment.datagsm.sdk.oauth.http.HttpClient;
import team.themoment.datagsm.sdk.oauth.http.JsonUtil;
import team.themoment.datagsm.sdk.oauth.model.CommonApiResponse;
import team.themoment.datagsm.sdk.oauth.model.UserInfo;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Account API 구현
 */
public class AccountApiImpl implements AccountApi {
    private final HttpClient httpClient;
    private final String baseUrl;

    public AccountApiImpl(HttpClient httpClient, String baseUrl) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
    }

    @Override
    public UserInfo getUserInfo(String accessToken) {
        Map<String, String> headers = createAuthHeaders(accessToken);

        String responseBody = httpClient.get(
                baseUrl + "/v1/account/my",
                headers,
                null
        );

        Type type = new TypeToken<CommonApiResponse<UserInfo>>(){}.getType();
        CommonApiResponse<UserInfo> apiResponse = JsonUtil.fromJson(responseBody, type);
        return apiResponse.getData();
    }

    private Map<String, String> createAuthHeaders(String accessToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);
        return headers;
    }
}
