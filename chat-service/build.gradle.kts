import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.4.0"
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
  kotlin("jvm") version "1.4.10"
  kotlin("plugin.spring") version "1.4.10"
}

group = "com.eemf"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  mavenCentral()
  maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("com.github.javafaker:javafaker:1.0.2")

  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains:markdown:0.3.1")

  //Coroutine support
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  testImplementation("app.cash.turbine:turbine:0.3.0")

  //WebFlux + R2DBC
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
  implementation("io.r2dbc:r2dbc-h2")

  //RSocket
  implementation("org.springframework.boot:spring-boot-starter-rsocket")

  runtimeOnly("com.h2database:h2")

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
