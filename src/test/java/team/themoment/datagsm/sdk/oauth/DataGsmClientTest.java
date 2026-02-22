package team.themoment.datagsm.sdk.oauth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DataGsmClient")
class DataGsmClientTest {

    @Nested
    @DisplayName("Builder")
    class Builder {

        @Test
        @DisplayName("clientId와 clientSecret으로 클라이언트를 생성한다")
        void buildSucceeds() {
            assertDoesNotThrow(() -> {
                DataGsmClient client = DataGsmClient.builder("client-id", "client-secret").build();
                client.close();
            });
        }

        @Test
        @DisplayName("clientId가 null이면 IllegalArgumentException이 발생한다")
        void nullClientIdThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> DataGsmClient.builder(null, "secret"));
        }

        @Test
        @DisplayName("clientId가 빈 문자열이면 IllegalArgumentException이 발생한다")
        void emptyClientIdThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> DataGsmClient.builder("", "secret"));
        }

        @Test
        @DisplayName("clientSecret이 null이면 IllegalArgumentException이 발생한다")
        void nullClientSecretThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> DataGsmClient.builder("client-id", null));
        }

        @Test
        @DisplayName("clientSecret이 빈 문자열이면 IllegalArgumentException이 발생한다")
        void emptyClientSecretThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> DataGsmClient.builder("client-id", ""));
        }

        @Test
        @DisplayName("try-with-resources로 안전하게 닫을 수 있다")
        void closeWithTryWithResources() {
            assertDoesNotThrow(() -> {
                try (DataGsmClient client = DataGsmClient.builder("client-id", "secret").build()) {
                    assertNotNull(client);
                }
            });
        }
    }
}