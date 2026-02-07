## DataGSM Oauth SDK for Java
[![JitPack](https://jitpack.io/v/themoment-team/datagsm-openapi-sdk-java.svg)](https://jitpack.io/#themoment-team/datagsm-openapi-sdk-java)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-13%2B-blue.svg)](https://www.java.com/)

DataGSM의 OAuth를 추상화된 환경에서 제공합니다.
### 설치 - Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.themoment-team</groupId>
    <artifactId>datagsm-oauth-sdk-java</artifactId>
    <version>1.0.0</version>
</dependency>
```
### 설치 - Gradle
```groovy
// Groovy DSL
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.themoment-team:datagsm-oauth-sdk-java:1.0.0'
}
```

```kotlin
// Kotlin DSL
repositories {
    maven { url("https://jitpack.io") }
}

dependencies {
    implementation("com.github.themoment-team:datagsm-oauth-sdk-java:1.0.0")
}
```

### 사용법
자세한 사용법은 [기술 문서](https://datagsm-front-client.vercel.app/docs/oauth/sdk/java)를 참고하십시오.