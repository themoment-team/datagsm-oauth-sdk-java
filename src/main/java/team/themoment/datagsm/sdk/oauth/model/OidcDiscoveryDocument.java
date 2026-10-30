package team.themoment.datagsm.sdk.oauth.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * OpenID Connect Discovery 문서 ({@code GET /.well-known/openid-configuration})
 *
 * @see <a href="https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata">OIDC Discovery 1.0</a>
 */
public class OidcDiscoveryDocument {
    @SerializedName("issuer")
    private String issuer;

    @SerializedName("authorization_endpoint")
    private String authorizationEndpoint;

    @SerializedName("token_endpoint")
    private String tokenEndpoint;

    @SerializedName("jwks_uri")
    private String jwksUri;

    @SerializedName("userinfo_endpoint")
    private String userinfoEndpoint;

    @SerializedName("end_session_endpoint")
    private String endSessionEndpoint;

    @SerializedName("response_types_supported")
    private List<String> responseTypesSupported;

    @SerializedName("grant_types_supported")
    private List<String> grantTypesSupported;

    @SerializedName("subject_types_supported")
    private List<String> subjectTypesSupported;

    @SerializedName("id_token_signing_alg_values_supported")
    private List<String> idTokenSigningAlgValuesSupported;

    @SerializedName("code_challenge_methods_supported")
    private List<String> codeChallengeMethodsSupported;

    @SerializedName("scopes_supported")
    private List<String> scopesSupported;

    @SerializedName("token_endpoint_auth_methods_supported")
    private List<String> tokenEndpointAuthMethodsSupported;

    @SerializedName("claims_supported")
    private List<String> claimsSupported;

    public OidcDiscoveryDocument() {}

    public String getIssuer() {
        return issuer;
    }

    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public String getJwksUri() {
        return jwksUri;
    }

    public String getUserinfoEndpoint() {
        return userinfoEndpoint;
    }

    /**
     * IdP 세션 로그아웃 엔드포인트 ({@code POST}, 브라우저 쿠키 기반)
     * <p>
     * 서버의 로그아웃 엔드포인트가 OIDC RP-Initiated Logout 규격(GET 리다이렉트)이 아니어서
     * 서버가 Discovery에서 {@code end_session_endpoint}를 내려주지 않게 되었습니다.
     * 서버가 값을 내려주면 그대로 반환하고, 없으면 null을 반환합니다.
     *
     * @deprecated {@link team.themoment.datagsm.sdk.oauth.DataGsmOAuthClient#getLogoutEndpoint()}를 사용하세요.
     * 이 메서드는 Discovery 값이 아니라 SDK가 고정 경로 {@code /v1/oauth/logout}으로 만든 URL을 반환합니다.
     */
    @Deprecated
    public String getEndSessionEndpoint() {
        return endSessionEndpoint;
    }

    public List<String> getResponseTypesSupported() {
        return nonNull(responseTypesSupported);
    }

    public List<String> getGrantTypesSupported() {
        return nonNull(grantTypesSupported);
    }

    public List<String> getSubjectTypesSupported() {
        return nonNull(subjectTypesSupported);
    }

    public List<String> getIdTokenSigningAlgValuesSupported() {
        return nonNull(idTokenSigningAlgValuesSupported);
    }

    public List<String> getCodeChallengeMethodsSupported() {
        return nonNull(codeChallengeMethodsSupported);
    }

    public List<String> getScopesSupported() {
        return nonNull(scopesSupported);
    }

    public List<String> getTokenEndpointAuthMethodsSupported() {
        return nonNull(tokenEndpointAuthMethodsSupported);
    }

    public List<String> getClaimsSupported() {
        return nonNull(claimsSupported);
    }

    private static List<String> nonNull(List<String> values) {
        return values != null ? Collections.unmodifiableList(values) : Collections.emptyList();
    }

    @Override
    public String toString() {
        return "OidcDiscoveryDocument{" +
                "issuer='" + issuer + '\'' +
                ", authorizationEndpoint='" + authorizationEndpoint + '\'' +
                ", tokenEndpoint='" + tokenEndpoint + '\'' +
                ", jwksUri='" + jwksUri + '\'' +
                ", userinfoEndpoint='" + userinfoEndpoint + '\'' +
                ", endSessionEndpoint='" + endSessionEndpoint + '\'' +
                '}';
    }
}
