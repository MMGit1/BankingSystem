plugins {
    id("java")
}

group = "ru.merkushev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":messaging-contracts"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.5"))
    implementation(project(":bank-service:domain"))

    implementation("org.springframework.security:spring-security-core")
    implementation("org.springframework.security:spring-security-crypto")
    compileOnly("org.springframework:spring-context:7.0.6")
    compileOnly("org.springframework:spring-tx:7.0.6")
    compileOnly("jakarta.validation:jakarta.validation-api:3.1.1")

    compileOnly("org.projectlombok:lombok:1.18.44")
    annotationProcessor("org.projectlombok:lombok:1.18.44")

    testCompileOnly("org.projectlombok:lombok:1.18.44")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.44")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
}

tasks.test {
    useJUnitPlatform()
}