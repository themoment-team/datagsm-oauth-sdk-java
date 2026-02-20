# DataGSM OAuth SDK for Java

DataGSM OAuth 2.0 인증 서버를 위한 공식 Java SDK입니다.

## Features

- OAuth 2.0 표준 준수 (RFC 6749)
- PKCE 지원 (RFC 7636) - 모바일/SPA 앱 보안 강화
- 3가지 Grant Type 지원
  - Authorization Code Grant
  - Refresh Token Grant
  - Client Credentials Grant
- 자동 PKCE 생성 - Code Verifier/Challenge 자동 처리
- 타입 안전성 - 강타입 모델 사용

## Quick Start

### 1. 의존성 추가

```xml
<!-- Maven -->
<dependency>
    <groupId>team.themoment</groupId>
    <artifactId>datagsm-oauth-sdk</artifactId>
    <version>1.0.2</version>
</dependency>
```

```gradle
// Gradle
implementation 'team.themoment:datagsm-oauth-sdk:1.0.2'
```

### 2. Client 생성

```java
DataGsmClient client = DataGsmClient.builder("your-client-id", "your-client-secret")
        .build();
```

기본 서버 URL은 다음과 같습니다.

- 인증 서버: `https://oauth.data.hellogsm.kr`
- 유저 정보 서버: `https://oauth-userinfo.data.hellogsm.kr`

### 3. OAuth 인증 플로우

#### Step 1: Authorization URL 생성

```java
String redirectUri = "https://myapp.com/callback";

AuthorizationUrlBuilder urlBuilder = client.createAuthorizationUrl(redirectUri)
        .enablePkce()
        .state("random-csrf-token")
        .scope("read write");

String authUrl = urlBuilder.build();
String codeVerifier = urlBuilder.getCodeVerifier(); // PKCE 사용 시 저장 필수
```

#### Step 2: Authorization Code 받기

사용자가 로그인 후 Redirect URI로 Code가 전달됩니다.

```
https://myapp.com/callback?code=abc123&state=random-csrf-token
```

#### Step 3: Access Token 교환

```java
TokenResponse tokens = client.exchangeCodeForToken(
        authorizationCode,
        redirectUri,
        codeVerifier  // PKCE 사용 시
);

String accessToken = tokens.getAccessToken();
String refreshToken = tokens.getRefreshToken();
```

#### Step 4: 사용자 정보 조회

```java
UserInfo userInfo = client.getUserInfo(accessToken);
System.out.println("Email: " + userInfo.getEmail());

if (userInfo.isStudent()) {
    Student student = userInfo.getStudent();
    System.out.println("Name: " + student.getName());
    System.out.println("Grade: " + student.getGrade());
}
```

## 상세 가이드

### Authorization Code Flow (표준)

가장 안전한 OAuth 플로우입니다. 웹 애플리케이션에 적합합니다.

```java
// 1. Authorization URL 생성
AuthorizationUrlBuilder builder = client.createAuthorizationUrl("https://myapp.com/callback")
        .state("csrf-protection-token")
        .scope("read write");

String authUrl = builder.build();

// 2. 사용자 리다이렉트 → 로그인 → Authorization Code 획득

// 3. Token 교환
TokenResponse tokens = client.exchangeCodeForToken(code, redirectUri);
```

### PKCE Flow (Public Client 권장)

모바일 앱, SPA(Single Page Application)에서는 반드시 PKCE를 사용하세요.

```java
// PKCE 자동 생성 (S256 메소드)
AuthorizationUrlBuilder builder = client.createAuthorizationUrl("myapp://callback")
        .enablePkce();

String authUrl = builder.build();
String codeVerifier = builder.getCodeVerifier(); // 저장 필수

// Token 교환 시 Code Verifier 전달 (client_secret 불필요)
TokenResponse tokens = client.exchangeCodeForToken(code, redirectUri, codeVerifier);
```

PKCE Code Verifier를 직접 지정하는 경우:

```java
AuthorizationUrlBuilder builder = client.createAuthorizationUrl(redirectUri)
        .enablePkce("your-random-43-128-chars-string", "S256");  // S256 또는 plain
```

### Refresh Token Flow

Access Token 만료 시 Refresh Token으로 갱신합니다.

```java
// 기본 갱신
TokenResponse newTokens = client.refreshToken(refreshToken);

// Scope 축소 (다운스코핑)
TokenResponse limitedTokens = client.refreshToken(refreshToken, "read");
```

### Client Credentials Flow

서버 간 통신에 사용됩니다. 사용자 없이 클라이언트 자격증명만으로 토큰을 발급합니다.

```java
// 기본
TokenResponse tokens = client.getClientCredentialsToken();

// Scope 지정
TokenResponse tokens = client.getClientCredentialsToken("api.read api.write");
```

Client Credentials Grant는 Refresh Token을 발급하지 않습니다.

## 보안 권장사항

### PKCE 사용 (Public Client)

모바일 앱, SPA는 반드시 PKCE를 활성화하세요.

```java
builder.enablePkce();
```

### State 파라미터 사용

CSRF 공격을 방지하기 위해 state 파라미터를 사용하세요.

```java
String state = generateRandomString();
builder.state(state);

// 콜백에서 검증
if (!receivedState.equals(state)) {
    throw new SecurityException("Invalid state");
}
```

### Redirect URI 검증

Authorization Server에 등록된 Redirect URI만 사용하세요.

### Token 저장

- Access Token: 짧은 수명 (1시간), 메모리에 저장
- Refresh Token: 긴 수명 (30일), 안전한 저장소에 암호화하여 저장

## 응답 모델

### TokenResponse

| 필드 | 타입 | 설명 |
|------|------|------|
| `accessToken` | `String` | Access Token |
| `tokenType` | `String` | 토큰 타입 ("Bearer") |
| `expiresIn` | `Long` | 만료 시간 (초) |
| `refreshToken` | `String` | Refresh Token (Client Credentials Grant는 null) |
| `scope` | `String` | 부여된 권한 범위 |

### UserInfo

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | `Long` | 계정 ID |
| `email` | `String` | 이메일 |
| `role` | `AccountRole` | 계정 역할 (`ROOT`, `ADMIN`, `USER`, `API_KEY_USER`) |
| `isStudent` | `Boolean` | 학생 여부 |
| `student` | `Student` | 학생 정보 (학생인 경우) |

### Student

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | `Long` | 학생 ID |
| `name` | `String` | 이름 |
| `sex` | `Sex` | 성별 (`MAN`, `WOMAN`) |
| `email` | `String` | 이메일 |
| `grade` | `Integer` | 학년 |
| `classNum` | `Integer` | 반 |
| `number` | `Integer` | 번호 |
| `studentNumber` | `Integer` | 학번 |
| `major` | `Major` | 전공 (`SW_DEVELOPMENT`, `SMART_IOT`, `AI`) |
| `role` | `StudentRole` | 학생 역할 (`STUDENT_COUNCIL`, `DORMITORY_MANAGER`, `GENERAL_STUDENT`) |
| `dormitoryFloor` | `Integer` | 기숙사 층 |
| `dormitoryRoom` | `Integer` | 기숙사 호실 |
| `isLeaveSchool` | `Boolean` | 자퇴 여부 |
| `majorClub` | `ClubInfo` | 전공동아리 정보 |
| `jobClub` | `ClubInfo` | 취업동아리 정보 |
| `autonomousClub` | `ClubInfo` | 창체동아리 정보 |

### ClubInfo

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | `Long` | 동아리 ID |
| `name` | `String` | 동아리 이름 |
| `type` | `ClubType` | 동아리 유형 (`MAJOR_CLUB`, `JOB_CLUB`, `AUTONOMOUS_CLUB`) |

## 고급 설정

### Custom HTTP Client

`HttpClient` 인터페이스를 구현하여 커스텀 HTTP 클라이언트를 사용할 수 있습니다.

```java
HttpClient customHttpClient = new MyCustomHttpClient();

DataGsmClient client = DataGsmClient.builder(clientId, clientSecret)
        .httpClient(customHttpClient)
        .build();
```

### Custom Base URL

```java
DataGsmClient client = DataGsmClient.builder(clientId, clientSecret)
        .authorizationBaseUrl("https://custom-auth.example.com")
        .userInfoBaseUrl("https://custom-userinfo.example.com")
        .build();
```

### 리소스 정리

`DataGsmClient`는 `AutoCloseable`을 구현하므로 try-with-resources를 사용할 수 있습니다.

```java
try (DataGsmClient client = DataGsmClient.builder(clientId, clientSecret).build()) {
    TokenResponse tokens = client.getClientCredentialsToken();
    // ...
}
```

## 에러 처리

| 예외 클래스 | HTTP 상태코드 | 설명 |
|-------------|-------------|------|
| `BadRequestException` | 400 | 잘못된 요청 |
| `UnauthorizedException` | 401 | 인증 실패 |
| `ForbiddenException` | 403 | 권한 없음 |
| `NotFoundException` | 404 | 리소스 없음 |
| `RateLimitException` | 429 | 요청 횟수 초과 |
| `ServerErrorException` | 500, 502, 503, 504 | 서버 에러 |
| `DataGsmException` | 그 외 | 기타 에러 (네트워크 에러 포함) |

`DataGsmException.hasStatusCode()`로 HTTP 에러인지 네트워크 에러인지 구분할 수 있습니다.

```java
try {
    TokenResponse tokens = client.exchangeCodeForToken(code, redirectUri);
} catch (UnauthorizedException e) {
    // 401: 인증 실패
} catch (BadRequestException e) {
    // 400: 잘못된 요청
} catch (NotFoundException e) {
    // 404: 리소스 없음
} catch (DataGsmException e) {
    if (e.hasStatusCode()) {
        System.err.println("HTTP " + e.getStatusCode() + " error: " + e.getMessage());
    } else {
        System.err.println("Network error: " + e.getMessage());
    }
}
```

## Migration Guide (v1.x → v2.x)

### 변경사항

| v1.x (레거시) | v2.x (표준 OAuth 2.0) |
|--------------|---------------------|
| `exchangeToken(code)` | `exchangeCodeForToken(code, redirectUri)` |
| `refreshToken(token)` | `refreshToken(token)` (동일) |
| N/A | `createAuthorizationUrl(redirectUri)` (신규) |
| N/A | `getClientCredentialsToken()` (신규) |
| N/A | PKCE 지원 (신규) |

### 마이그레이션 예제

Before (v1.x):

```java
TokenResponse tokens = client.exchangeToken(code);
```

After (v2.x):

```java
// Redirect URI 필수
TokenResponse tokens = client.exchangeCodeForToken(code, redirectUri);

// PKCE 사용 시
TokenResponse tokens = client.exchangeCodeForToken(code, redirectUri, codeVerifier);
```

## License

MIT License

## References

- [OAuth 2.0 (RFC 6749)](https://datatracker.ietf.org/doc/html/rfc6749)
- [PKCE (RFC 7636)](https://datatracker.ietf.org/doc/html/rfc7636)
- [Bearer Token (RFC 6750)](https://datatracker.ietf.org/doc/html/rfc6750)