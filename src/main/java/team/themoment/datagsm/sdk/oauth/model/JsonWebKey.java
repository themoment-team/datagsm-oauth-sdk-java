package team.themoment.datagsm.sdk.oauth.model;

/**
 * RSA 공개키 JWK (RFC 7517). 서버 RsaPublicJwkDto와 대응합니다.
 */
public class JsonWebKey {
    private String kty;
    private String kid;
    private String n;
    private String e;
    private String use;
    private String alg;

    public JsonWebKey() {}

    /**
     * 키 타입 (항상 {@code RSA})
     */
    public String getKty() {
        return kty;
    }

    /**
     * 키 ID. JWT 헤더의 {@code kid}와 비교해 검증 키를 고릅니다.
     */
    public String getKid() {
        return kid;
    }

    /**
     * RSA 모듈러스 (Base64url, 패딩 없음)
     */
    public String getN() {
        return n;
    }

    /**
     * RSA 공개 지수 (Base64url, 패딩 없음)
     */
    public String getE() {
        return e;
    }

    /**
     * 키 용도 (선택, 현재 서버는 내려주지 않을 수 있음)
     */
    public String getUse() {
        return use;
    }

    /**
     * 알고리즘 (선택, 현재 서버는 내려주지 않을 수 있음)
     */
    public String getAlg() {
        return alg;
    }

    @Override
    public String toString() {
        return "JsonWebKey{" +
                "kty='" + kty + '\'' +
                ", kid='" + kid + '\'' +
                ", use='" + use + '\'' +
                ", alg='" + alg + '\'' +
                '}';
    }
}
