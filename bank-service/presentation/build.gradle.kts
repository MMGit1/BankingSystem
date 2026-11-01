plugins {
    id("java")
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.merkushev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":messaging-contracts"))
    implementation(project(":bank-service:domain"))
    implementation(project(":bank-service:application"))
    implementation(project(":bank-service:infrastructure"))

    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")
    runtimeOnly("org.postgresql:postgresql")

    compileOnly("org.projectlombok:lombok:1.18.44")
    annotationProcessor("org.projectlombok:lombok:1.18.44")

    testCompileOnly("org.projectlombok:lombok:1.18.44")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.44")

    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    testImplementation("org.testcontainers:testcontainers-junit-jupiter:2.0.4")
    testImplementation("org.testcontainers:testcontainers-postgresql:2.0.4")
}

tasks.test {
    useJUnitPlatform()
}