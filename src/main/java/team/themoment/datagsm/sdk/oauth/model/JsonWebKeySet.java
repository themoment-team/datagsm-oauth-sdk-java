package team.themoment.datagsm.sdk.oauth.model;

import java.util.Collections;
import java.util.List;

/**
 * JWK Set ({@code GET /v1/oauth/jwks})
 */
public class JsonWebKeySet {
    private List<JsonWebKey> keys;

    public JsonWebKeySet() {}

    /**
     * 공개키 목록. null을 반환하지 않습니다.
     */
    public List<JsonWebKey> getKeys() {
        return keys != null ? Collections.unmodifiableList(keys) : Collections.emptyList();
    }

    /**
     * kid로 공개키를 찾습니다.
     *
     * @param kid 키 ID (JWT 헤더의 kid)
     * @return 일치하는 키, 없으면 null
     */
    public JsonWebKey findByKid(String kid) {
        if (kid == null) {
            return null;
        }
        for (JsonWebKey key : getKeys()) {
            if (kid.equals(key.getKid())) {
                return key;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "JsonWebKeySet{keys=" + keys + '}';
    }
}
