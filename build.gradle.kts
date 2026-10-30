import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar

plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.37.0"
}

group = "io.github.themoment-team"
version = "2.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    sourceCompatibility = JavaVersion.VERSION_13
    targetCompatibility = JavaVersion.VERSION_13
}

repositories {
    mavenCentral()
}

dependencies {
    // HTTP Client
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // JSON Processing
    implementation("com.google.code.gson:gson:2.11.0")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.mockito:mockito-core:5.14.2")
}

tasks.test {
    useJUnitPlatform()
}

// Maven Central 배포 설정. CI에서는 ORG_GRADLE_PROJECT_* 환경 변수로 인증 정보와 서명 키를 전달합니다.
mavenPublishing {
    configure(JavaLibrary(javadocJar = JavadocJar.Javadoc(), sourcesJar = SourcesJar.Sources()))
    publishToMavenCentral()
    // 서명 키가 있을 때만 서명합니다. 키가 없어도 publishToMavenLocal로 로컬 검증을 할 수 있고,
    // 서명 없이 Central에 올리면 Portal 검증에서 거부되므로 잘못 배포될 위험은 없습니다.
    if (providers.gradleProperty("signingInMemoryKey").isPresent) {
        signAllPublications()
    }
    coordinates(group.toString(), "datagsm-oauth-sdk-java", version.toString())

    pom {
        name = "DataGSM Oauth SDK for Java"
        description = "Java SDK for DataGSM Oauth"
        url = "https://github.com/themoment-team/datagsm-oauth-sdk-java"

        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
            }
        }

        developers {
            developer {
                id = "themoment"
                name = "themoment-team"
                email = "datagsm.oauth@gmail.com"
            }
        }

        scm {
            connection = "scm:git:git://github.com/themoment-team/datagsm-oauth-sdk-java.git"
            developerConnection = "scm:git:ssh://github.com:themoment-team/datagsm-oauth-sdk-java.git"
            url = "https://github.com/themoment-team/datagsm-oauth-sdk-java"
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(13)
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
