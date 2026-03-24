package team.themoment.datagsm.sdk.oauth.client;

import team.themoment.datagsm.sdk.oauth.http.HttpClient;
import team.themoment.datagsm.sdk.oauth.http.JsonUtil;
import team.themoment.datagsm.sdk.oauth.model.UserInfo;

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
                baseUrl + "/userinfo",
                headers,
                null
        );

        return JsonUtil.fromJson(responseBody, UserInfo.class);
    }

    private Map<String, String> createAuthHeaders(String accessToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);
        return headers;
    }
}
