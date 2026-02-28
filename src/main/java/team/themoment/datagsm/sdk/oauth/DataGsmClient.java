package team.themoment.datagsm.sdk.oauth;

import team.themoment.datagsm.sdk.oauth.http.HttpClient;
import team.themoment.datagsm.sdk.oauth.model.*;

/**
 * @deprecated {@link DataGsmOAuthClient}를 사용하세요.
 *             이 클래스는 하위 호환성을 위해 유지되며 이후 버전에서 제거될 예정입니다.
 * @see DataGsmOAuthClient
 */
@Deprecated(since = "1.0.1", forRemoval = true)
public class DataGsmClient implements AutoCloseable {

    private final DataGsmOAuthClient delegate;

    private DataGsmClient(Builder builder) {
        DataGsmOAuthClient.Builder delegateBuilder = DataGsmOAuthClient.builder(builder.clientId, builder.clientSecret);
        if (builder.authorizationBaseUrl != null) {
            delegateBuilder.authorizationBaseUrl(builder.authorizationBaseUrl);
        }
        if (builder.userInfoBaseUrl != null) {
            delegateBuilder.userInfoBaseUrl(builder.userInfoBaseUrl);
        }
        if (builder.httpClient != null) {
            delegateBuilder.httpClient(builder.httpClient);
        }
        this.delegate = delegateBuilder.build();
    }

    /**
     * @deprecated {@link DataGsmOAuthClient#createAuthorizationUrl(String)}를 사용하세요.
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public AuthorizationUrlBuilder createAuthorizationUrl(String redirectUri) {
        return delegate.createAuthorizationUrl(redirectUri);
    }

    /**
     * @deprecated {@link DataGsmOAuthClient#exchangeCodeForToken(String, String)}를 사용하세요.
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public TokenResponse exchangeCodeForToken(String code, String redirectUri) {
        return delegate.exchangeCodeForToken(code, redirectUri);
    }

    /**
     * @deprecated {@link DataGsmOAuthClient#exchangeCodeForToken(String, String, String)}를 사용하세요.
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public TokenResponse exchangeCodeForToken(String code, String redirectUri, String codeVerifier) {
        return delegate.exchangeCodeForToken(code, redirectUri, codeVerifier);
    }

    /**
     * @deprecated {@link DataGsmOAuthClient#refreshToken(String)}를 사용하세요.
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public TokenResponse refreshToken(String refreshToken) {
        return delegate.refreshToken(refreshToken);
    }

    /**
     * @deprecated {@link DataGsmOAuthClient#refreshToken(String, String)}를 사용하세요.
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public TokenResponse refreshToken(String refreshToken, String scope) {
        return delegate.refreshToken(refreshToken, scope);
    }

    /**
     * @deprecated {@link DataGsmOAuthClient#getClientCredentialsToken()}를 사용하세요.
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public TokenResponse getClientCredentialsToken() {
        return delegate.getClientCredentialsToken();
    }

    /**
     * @deprecated {@link DataGsmOAuthClient#getClientCredentialsToken(String)}를 사용하세요.
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public TokenResponse getClientCredentialsToken(String scope) {
        return delegate.getClientCredentialsToken(scope);
    }

    /**
     * @deprecated {@link DataGsmOAuthClient#issueOAuthCode(OAuthCodeRequest)}를 사용하세요.
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public OAuthCodeResponse issueOAuthCode(OAuthCodeRequest request) {
        return delegate.issueOAuthCode(request);
    }

    /**
     * @deprecated {@link DataGsmOAuthClient#getUserInfo(String)}를 사용하세요.
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public UserInfo getUserInfo(String accessToken) {
        return delegate.getUserInfo(accessToken);
    }

    @Override
    public void close() {
        delegate.close();
    }

    /**
     * @deprecated {@link DataGsmOAuthClient#builder(String, String)}를 사용하세요.
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static Builder builder(String clientId, String clientSecret) {
        return new Builder(clientId, clientSecret);
    }

    /**
     * @deprecated {@link DataGsmOAuthClient.Builder}를 사용하세요.
     */
    @Deprecated(since = "1.0.1", forRemoval = true)
    public static class Builder {
        private final String clientId;
        private final String clientSecret;
        private String authorizationBaseUrl;
        private String userInfoBaseUrl;
        private HttpClient httpClient;

        private Builder(String clientId, String clientSecret) {
            if (clientId == null || clientId.trim().isEmpty()) {
                throw new IllegalArgumentException("Client id is required");
            }
            if (clientSecret == null || clientSecret.trim().isEmpty()) {
                throw new IllegalArgumentException("Client secret is required");
            }
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        /**
         * @deprecated {@link DataGsmOAuthClient.Builder#authorizationBaseUrl(String)}를 사용하세요.
         */
        @Deprecated(since = "1.0.1", forRemoval = true)
        public Builder authorizationBaseUrl(String authorizationBaseUrl) {
            this.authorizationBaseUrl = authorizationBaseUrl;
            return this;
        }

        /**
         * @deprecated {@link DataGsmOAuthClient.Builder#userInfoBaseUrl(String)}를 사용하세요.
         */
        @Deprecated(since = "1.0.1", forRemoval = true)
        public Builder userInfoBaseUrl(String userInfoBaseUrl) {
            this.userInfoBaseUrl = userInfoBaseUrl;
            return this;
        }

        /**
         * @deprecated {@link DataGsmOAuthClient.Builder#httpClient(HttpClient)}를 사용하세요.
         */
        @Deprecated(since = "1.0.1", forRemoval = true)
        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        /**
         * @deprecated {@link DataGsmOAuthClient.Builder#build()}를 사용하세요.
         */
        @Deprecated(since = "1.0.1", forRemoval = true)
        public DataGsmClient build() {
            return new DataGsmClient(this);
        }
    }
}
