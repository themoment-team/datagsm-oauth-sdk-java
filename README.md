## DataGSM Oauth SDK for Java
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-13%2B-blue.svg)](https://www.java.com/)

DataGSM의 OAuth를 추상화된 환경에서 제공합니다.
### 설치
Maven Central에 배포됩니다. 별도 저장소나 인증 토큰 없이 `mavenCentral()`만 있으면 됩니다.

#### Maven
```xml
<dependency>
    <groupId>io.github.themoment-team</groupId>
    <artifactId>datagsm-oauth-sdk-java</artifactId>
    <version>2.0.1</version>
</dependency>
```

#### Gradle
```groovy
// Groovy DSL
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.themoment-team:datagsm-oauth-sdk-java:2.0.1'
}
```

```kotlin
// Kotlin DSL
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.themoment-team:datagsm-oauth-sdk-java:2.0.1")
}
```

### 빠른 시작
```java
try (DataGsmOAuthClient client = DataGsmOAuthClient.builder("client-id", "client-secret").build()) {
    AuthorizationUrlBuilder url = client.createAuthorizationUrl("https://myapp.com/callback")
            .state(state)
            .nonce(nonce) // openid 요청 시 권장
            .scopes(List.of("openid", "my-app-id:account_read", "my-app-id:student_read"));
    // 사용자를 url.build()로 리다이렉트 → 콜백에서 code 수신

    TokenResponse token = client.exchangeCodeForToken(code, "https://myapp.com/callback");
    UserInfo userInfo = client.getUserInfo(token.getAccessToken());
}
```

- public client(모바일·SPA)는 `DataGsmOAuthClient.builder("client-id")`로 만들고 반드시 PKCE(`enablePkce()`, `exchangeCodeForToken(code, redirectUri, codeVerifier)`)를 사용합니다.
- 전체 예제는 [`OAuthExample`](src/main/java/team/themoment/datagsm/sdk/oauth/example/OAuthExample.java)을 참고하세요.

### Scope
scope는 `{appId}:{scopeName}` 형식입니다. `{appId}`는 DataGSM 애플리케이션 ID이며, 실제 값은 `client.getDiscoveryDocument().getScopesSupported()`로 확인할 수 있습니다. 인가 요청에서 scope를 생략하면 `account_read`, `student_read`만 부여됩니다.

| scope | `UserInfo`에서 채워지는 필드 | scope가 없을 때 |
|---|---|---|
| `openid` | 토큰 응답의 `idToken` | `idToken` null |
| `{appId}:account_read` | `id`, `email`, `role`, `status`, `objectType` | `/userinfo` 호출 불가 (403) |
| `{appId}:student_read` | `student`, `teacher` | 둘 다 null |
| `{appId}:club_read` | `clubs`, `student.majorClub`, `student.autonomousClub` | 빈 목록, null |
| `{appId}:project_read` | `projects` | 빈 목록 |

- scope가 있어도 계정 종류에 따라 `student`, `teacher` 중 하나만 채워집니다. 졸업생·자퇴생은 학번·기숙사 정보가 null일 수 있습니다.
- `getClubs()`, `getProjects()`는 null을 반환하지 않습니다.
- 서버가 SDK에 없는 enum 값을 내려주면 해당 필드는 예외 없이 null이 됩니다.

### 에러 처리
모든 예외는 `DataGsmException`을 상속하며, 서버 에러 본문을 파싱해 `getErrorCode()`, `getErrorDescription()`, `getResponseBody()`로 제공합니다.

- OAuth 에러(`/v1/oauth/token`): `errorCode`는 `invalid_grant`, `invalid_client` 같은 `error` 값입니다.
- CommonApiResponse 에러(`/userinfo` 인증 실패, rate limit 등): `errorCode`는 `UNAUTHORIZED`, `TOO_MANY_REQUESTS` 같은 `status` 값입니다.

### OpenID Connect
- `getDiscoveryDocument()`: `/.well-known/openid-configuration` 조회
- `getJwkSet()`: `/v1/oauth/jwks` 조회. SDK는 id_token 서명을 검증하지 않으므로, 이 공개키로 JWT 라이브러리에서 RS256 서명과 `iss`, `aud`, `exp`, `nonce`를 직접 검증하세요.
- `getLogoutEndpoint()`: IdP 세션 로그아웃 URL(`POST /v1/oauth/logout`). 브라우저 쿠키로만 동작하므로 브라우저에서 `fetch(url, { method: "POST", credentials: "include" })`로 호출합니다.

### 2.0.0 → 2.0.1 마이그레이션
배포 위치가 JitPack에서 Maven Central로 바뀌면서 groupId가 바뀌었습니다. 코드의 패키지(`import`)는 그대로입니다.

- 의존성을 `com.github.themoment-team:datagsm-oauth-sdk-java`에서 `io.github.themoment-team:datagsm-oauth-sdk-java`로 바꾸세요.
- JitPack 저장소(`https://jitpack.io`) 설정은 다른 의존성에서 쓰지 않는다면 지워도 됩니다.

### 1.x → 2.0 마이그레이션
| 1.x | 2.0 |
|---|---|
| `issueOAuthCode(OAuthCodeRequest)` | 제거 (서버 엔드포인트 삭제). `createAuthorizationUrl()` → `exchangeCodeForToken()` 사용 |
| `OAuthCodeRequest`, `OAuthCodeResponse` | 제거 |
| `ClubInfo` (`status`, `foundedYear`, `abolishedYear`) | `ClubSummary` (`id`, `name`, `type`). userinfo에 내려오지 않던 필드 제거 |
| `ClubStatus` | 제거 |
| `Student.getIsLeaveSchool()` / `setIsLeaveSchool()` | 제거. `student.getRole() == StudentRole.WITHDRAWN` 사용 (`isLeaveSchool()`은 deprecated로 유지) |
| `AccountRole.API_KEY_USER` | 제거 (서버에 없음) |
| `refreshToken(token, "a b")`, `getClientCredentialsToken("a b")` | 그대로 동작하며 공백으로 분리해 배열로 전송. `List<String>` 오버로드 추가 |
| `DataGsmOAuthClient.builder(id, null)` | 여전히 예외. public client는 `builder(id)` 사용 |
| `OAuthApi` 인터페이스 직접 구현 | 추가된 메서드(`List` scope 오버로드, `getDiscoveryDocument`, `getJwkSet`, `getLogoutEndpoint`) 구현 필요 |

자세한 사용법은 [기술 문서](https://docs.datagsm.kr/oauth/sdk/java)를 참고하십시오.