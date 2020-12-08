import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.20"
  id("com.google.protobuf") version "0.8.14"

  application
  id("idea")
}

repositories {
  jcenter()
}

group = "blocksize.assignment"
version = "1.0-SNAPSHOT"


ext["grpcVersion"] = "1.34.0"
ext["grpcKotlinVersion"] = "0.2.1"
ext["protobufVersion"] = "3.14.0"
ext["exposedVersion"] = "0.28.1"
ext["logbackVersion"] = "1.2.3"


dependencies {
  implementation(kotlin("stdlib"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
  implementation("javax.annotation:javax.annotation-api:1.3.2")
  api("io.grpc:grpc-kotlin-stub:${project.ext["grpcKotlinVersion"]}")
  runtimeOnly("io.grpc:grpc-netty:${project.ext["grpcVersion"]}")

  implementation("org.jetbrains.exposed:exposed-core:${project.ext["exposedVersion"]}")
  implementation("org.jetbrains.exposed:exposed-jdbc:${project.ext["exposedVersion"]}")
  implementation("com.zaxxer", "HikariCP", "3.4.5")
  implementation("org.postgresql:postgresql:42.2.18")

  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("ch.qos.logback:logback-core:${project.ext["logbackVersion"]}")
  implementation("ch.qos.logback:logback-classic:${project.ext["logbackVersion"]}")


  testImplementation(kotlin("test-junit5"))
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
  testRuntimeOnly ("com.h2database:h2:1.4.200")
}

java {
  sourceSets.getByName("main").resources.srcDir("src/main/proto")
}

tasks.test {
  useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
  kotlinOptions.jvmTarget = "11"
}

application {
  mainClass.set("blocksize.assignment.MainKt")
}


protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:${project.ext["protobufVersion"]}"
  }
  plugins {
    id("grpc") {
      artifact = "io.grpc:protoc-gen-grpc-java:${project.ext["grpcVersion"]}"
    }
    id("grpckt") {
      artifact = "io.grpc:protoc-gen-grpc-kotlin:${project.ext["grpcKotlinVersion"]}:jdk7@jar"
    }
  }
  generateProtoTasks {
    all().forEach {
      it.plugins {
        id("grpc")
        id("grpckt")
      }
    }
  }
}

kotlin {
  sourceSets.all {
    languageSettings.enableLanguageFeature("InlineClasses")
  }
}
