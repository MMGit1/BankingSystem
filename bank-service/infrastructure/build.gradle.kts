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
    implementation(project(":bank-service:domain"))
    implementation(project(":bank-service:application"))

    implementation("org.springframework.kafka:spring-kafka:4.0.5")
    implementation("org.springframework:spring-context")
    compileOnly("org.springframework.data:spring-data-jpa:4.0.4")
    implementation("org.hibernate.orm:hibernate-core:7.0.6.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("org.postgresql:postgresql:42.7.7")

    compileOnly("org.projectlombok:lombok:1.18.44")
    annotationProcessor("org.projectlombok:lombok:1.18.44")

    testCompileOnly("org.projectlombok:lombok:1.18.44")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.44")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}