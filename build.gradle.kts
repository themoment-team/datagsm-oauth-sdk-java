plugins {
    `java-library`
    `maven-publish`
}

group = "team.themoment.datagsm.sdk"
version = "1.0.0-M1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    sourceCompatibility = JavaVersion.VERSION_13
    targetCompatibility = JavaVersion.VERSION_13
    withSourcesJar()
    withJavadocJar()
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
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "oauth"
            version = project.version.toString()

            from(components["java"])

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
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(13)
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
