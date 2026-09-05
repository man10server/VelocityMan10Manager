import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("kapt") version "2.2.10"
    id("java")
    id("com.gradleup.shadow") version "9.0.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("org.ktorm:ktorm-core:4.1.1")
    implementation("com.mysql:mysql-connector-j:9.4.0")

    implementation("net.dv8tion:JDA:5.6.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.3")

    kapt("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
}
