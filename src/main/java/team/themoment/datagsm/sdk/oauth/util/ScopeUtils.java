package team.themoment.datagsm.sdk.oauth.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * OAuth scope 문자열/목록 변환 유틸리티
 * <p>
 * DataGSM scope는 {@code {applicationId}:{scopeName}} 형식이며(예: {@code my-app-id:account_read}),
 * OIDC 지시자인 {@code openid}만 접두어 없이 사용합니다.
 */
public final class ScopeUtils {
    private static final String WHITESPACE = "\\s+";
    private static final String SEPARATOR = " ";

    private ScopeUtils() {
    }

    /**
     * 공백으로 구분된 scope 문자열을 목록으로 분리합니다.
     * 빈 항목과 중복은 제거하고 입력 순서를 유지합니다.
     *
     * @param scope 공백 구분 scope 문자열 (null 허용)
     * @return 불변 scope 목록 (입력이 null이거나 비어 있으면 빈 목록)
     */
    public static List<String> split(String scope) {
        if (scope == null || scope.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<String> tokens = new ArrayList<>();
        for (String token : scope.trim().split(WHITESPACE)) {
            tokens.add(token);
        }
        return normalize(tokens);
    }

    /**
     * scope 목록에서 null·공백 항목과 중복을 제거합니다. 입력 순서는 유지합니다.
     *
     * @param scopes scope 목록 (null 허용)
     * @return 불변 scope 목록
     */
    public static List<String> normalize(Collection<String> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> unique = new LinkedHashSet<>();
        for (String scope : scopes) {
            if (scope != null && !scope.trim().isEmpty()) {
                unique.add(scope.trim());
            }
        }
        return Collections.unmodifiableList(new ArrayList<>(unique));
    }

    /**
     * scope 목록을 공백으로 구분된 문자열로 합칩니다.
     *
     * @param scopes scope 목록 (null 허용)
     * @return 공백 구분 문자열 (비어 있으면 빈 문자열)
     */
    public static String join(Collection<String> scopes) {
        return String.join(SEPARATOR, normalize(scopes));
    }
}
