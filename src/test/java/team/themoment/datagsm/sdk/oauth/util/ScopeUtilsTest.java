package team.themoment.datagsm.sdk.oauth.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ScopeUtils")
class ScopeUtilsTest {

    @Test
    @DisplayName("공백으로 구분된 문자열을 순서를 유지하며 분리한다")
    void splitKeepsOrder() {
        List<String> scopes = ScopeUtils.split(" openid\tapp:account_read  app:student_read ");

        assertEquals(Arrays.asList("openid", "app:account_read", "app:student_read"), scopes);
    }

    @Test
    @DisplayName("null이나 공백 문자열은 빈 목록이 된다")
    void splitBlank() {
        assertTrue(ScopeUtils.split(null).isEmpty());
        assertTrue(ScopeUtils.split("   ").isEmpty());
    }

    @Test
    @DisplayName("중복과 빈 항목을 제거한다")
    void normalizeRemovesDuplicatesAndBlanks() {
        List<String> scopes = ScopeUtils.normalize(Arrays.asList("a:x", null, " ", "a:x", "a:y"));

        assertEquals(Arrays.asList("a:x", "a:y"), scopes);
    }

    @Test
    @DisplayName("목록을 공백 구분 문자열로 합친다")
    void join() {
        assertEquals("a:x a:y", ScopeUtils.join(Arrays.asList("a:x", "a:y")));
        assertEquals("", ScopeUtils.join(null));
    }
}
