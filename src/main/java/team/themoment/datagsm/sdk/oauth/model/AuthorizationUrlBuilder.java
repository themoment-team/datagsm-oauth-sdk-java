package team.themoment.datagsm.sdk.oauth.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import team.themoment.datagsm.sdk.oauth.util.ScopeUtils;

/**
 * OAuth 2.0 Authorization URL 빌더
 * PKCE (RFC 7636) 지원
 */
public class AuthorizationUrlBuilder {
    private static final String PKCE_METHOD_S256 = "S256";
    private static final String PKCE_METHOD_PLAIN = "plain";

    private final String authorizationEndpoint;
    private final String clientId;
    private String redirectUri;
    private String state;
    private String scope;
    private String nonce;
    private String codeChallenge;
    private String codeChallengeMethod;
    private String codeVerifier;

    public AuthorizationUrlBuilder(String authorizationEndpoint, String clientId) {
        this.authorizationEndpoint = authorizationEndpoint;
        this.clientId = clientId;
    }

    /**
     * Redirect URI 설정 (필수)
     */
    public AuthorizationUrlBuilder redirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }

    /**
     * State 파라미터 설정 (CSRF 방지용, 권장)
     */
    public AuthorizationUrlBuilder state(String state) {
        this.state = state;
        return this;
    }

    /**
     * Scope 설정 (선택, 공백 구분)
     * <p>
     * 형식은 {@code {appId}:{scopeName}}이며 OIDC를 쓰려면 {@code openid}를 함께 넣습니다.
     * 예: {@code "openid my-app-id:account_read my-app-id:student_read"}.
     * 생략하면 서버 기본값(account_read, student_read)이 부여됩니다.
     */
    public AuthorizationUrlBuilder scope(String scope) {
        this.scope = scope;
        return this;
    }

    /**
     * Scope 목록 설정 (선택). 공백으로 합쳐서 전송합니다.
     *
     * @param scopes scope 목록 (예: {@code List.of("openid", "my-app-id:account_read")})
     */
    public AuthorizationUrlBuilder scopes(List<String> scopes) {
        String joined = ScopeUtils.join(scopes);
        this.scope = joined.isEmpty() ? null : joined;
        return this;
    }

    /**
     * OIDC nonce 설정 (선택, {@code openid} scope 요청 시 권장)
     * <p>
     * 서버는 이 값을 id_token의 {@code nonce} 클레임에 그대로 담습니다.
     * 세션에 저장해 두었다가 id_token 검증 시 일치하는지 확인하세요.
     */
    public AuthorizationUrlBuilder nonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    /**
     * PKCE 활성화 (S256 메소드)
     * Code Verifier를 자동 생성하고 Code Challenge를 계산합니다.
     * 
     * @return this (생성된 code_verifier는 getCodeVerifier()로 조회 가능)
     */
    public AuthorizationUrlBuilder enablePkce() {
        this.codeVerifier = generateCodeVerifier();
        this.codeChallenge = generateCodeChallenge(codeVerifier);
        this.codeChallengeMethod = PKCE_METHOD_S256;
        return this;
    }

    /**
     * PKCE 활성화 (수동 Code Verifier 지정)
     * <p>
     * 서버는 code_challenge_method를 대소문자까지 정확히 비교하므로
     * 입력값의 대소문자와 앞뒤 공백에 관계없이 {@code S256} 또는 {@code plain}으로 정규화해 전송합니다.
     *
     * <p>
     * {@code plain}은 하위 호환을 위해 서버가 계속 받지만 Discovery의 {@code code_challenge_methods_supported}에서는 빠졌습니다.
     * 새로 연동한다면 {@code S256}이나 {@link #enablePkce()}를 사용하세요.
     *
     * @param codeVerifier 43~128자의 랜덤 문자열
     * @param method "S256" 또는 "plain" (대소문자 무관)
     */
    public AuthorizationUrlBuilder enablePkce(String codeVerifier, String method) {
        if (codeVerifier == null || codeVerifier.isEmpty()) {
            throw new IllegalArgumentException("codeVerifier is required");
        }
        String normalizedMethod = normalizePkceMethod(method);

        this.codeVerifier = codeVerifier;
        this.codeChallengeMethod = normalizedMethod;
        this.codeChallenge = PKCE_METHOD_S256.equals(normalizedMethod)
                ? generateCodeChallenge(codeVerifier)
                : codeVerifier;

        return this;
    }

    private static String normalizePkceMethod(String method) {
        String trimmed = method != null ? method.trim() : "";
        if (PKCE_METHOD_S256.equalsIgnoreCase(trimmed)) {
            return PKCE_METHOD_S256;
        }
        if (PKCE_METHOD_PLAIN.equalsIgnoreCase(trimmed)) {
            return PKCE_METHOD_PLAIN;
        }
        throw new IllegalArgumentException("Invalid code_challenge_method: " + method);
    }

    /**
     * Authorization URL 생성
     */
    public String build() {
        if (redirectUri == null || redirectUri.isEmpty()) {
            throw new IllegalStateException("redirectUri is required");
        }

        StringBuilder url = new StringBuilder(authorizationEndpoint);
        url.append("?response_type=code");
        url.append("&client_id=").append(urlEncode(clientId));
        url.append("&redirect_uri=").append(urlEncode(redirectUri));

        if (state != null && !state.isEmpty()) {
            url.append("&state=").append(urlEncode(state));
        }

        if (scope != null && !scope.isEmpty()) {
            url.append("&scope=").append(urlEncode(scope));
        }

        if (nonce != null && !nonce.isEmpty()) {
            url.append("&nonce=").append(urlEncode(nonce));
        }

        if (codeChallenge != null && codeChallengeMethod != null) {
            url.append("&code_challenge=").append(urlEncode(codeChallenge));
            url.append("&code_challenge_method=").append(urlEncode(codeChallengeMethod));
        }

        return url.toString();
    }

    /**
     * 생성된 Code Verifier 조회 (PKCE 사용 시)
     * 토큰 교환 시 필요합니다.
     */
    public String getCodeVerifier() {
        return codeVerifier;
    }

    /**
     * Code Verifier 생성 (43~128자의 URL-safe 랜덤 문자열)
     */
    private String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] code = new byte[32];
        secureRandom.nextBytes(code);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(code);
    }

    /**
     * Code Challenge 생성 (SHA-256 해시)
     */
    private String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * URL 인코딩
     */
    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }
}
