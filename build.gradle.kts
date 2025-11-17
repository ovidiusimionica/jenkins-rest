import org.gradle.api.JavaVersion
import org.gradle.jvm.tasks.Jar
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.javadoc.Javadoc
import java.util.Date

plugins {
    id("project.java-conventions")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:3.1.0")
    implementation("org.eclipse.microprofile.rest.client:microprofile-rest-client-api:4.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.4")

    testImplementation("org.testng:testng:7.11.0")
    testImplementation("org.awaitility:awaitility:4.2.0")
    testImplementation("org.testcontainers:testcontainers:1.21.3")

    testRuntimeOnly("ch.qos.logback:logback-classic:1.5.21")
    testRuntimeOnly("io.smallrye.config:smallrye-config:3.7.1")
    testRuntimeOnly("org.jboss.resteasy.microprofile:microprofile-rest-client:3.0.1.Final")
    testRuntimeOnly("org.jboss.resteasy:resteasy-client:6.2.11.Final")
    testRuntimeOnly("org.jboss.resteasy:resteasy-jackson2-provider:6.2.11.Final")
}

// Extra properties
val compatibilityVersion = JavaVersion.VERSION_21
val javadocPath = if (compatibilityVersion.isJava8) "" else "en/java/"

java {
    sourceCompatibility = compatibilityVersion
    targetCompatibility = compatibilityVersion
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:-options")
}

tasks.named("check") {
    dependsOn("intTest")
}

fun getGitCommitHash(): String {
    val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
        .redirectErrorStream(true)
        .start()
    return process.inputStream.bufferedReader().readText().trim()
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to "Jenkins REST client",
                "Implementation-Version" to archiveVersion.get(),
                "Git-Commit" to getGitCommitHash(),
                "Built-By" to System.getProperty("user.name"),
                "Built-Date" to Date(),
                "Built-JDK" to System.getProperty("java.version"),
                "Built-Gradle" to gradle.gradleVersion
            )
        )
    }
}

val intTest = tasks.register<Test>("intTest") {
    group = "Verification"
    description = "Integration tests - Jenkins must be running. See the README."
    useTestNG()
    include("**/**LiveTest*")
    maxParallelForks = 1
    systemProperty("user.timezone", "UTC")
    maxHeapSize = "2g"

    testLogging {
        showStandardStreams = true
        events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
    }

    val possibleUsernameApiToken = project.findProperty("testJenkinsUsernameApiToken")?.toString()
    val usernameApiToken = mutableMapOf<String, Any>()
    if (possibleUsernameApiToken != null) {
        usernameApiToken["test.jenkins.usernameApiToken"] = possibleUsernameApiToken
    }

    systemProperties(
        mapOf(
            "test.jenkins.usernamePassword" to project.property("testJenkinsUsernamePassword")
        ) + usernameApiToken
    )
}

tasks.withType<Javadoc>().configureEach {
    source = sourceSets.main.get().allJava
    (options as StandardJavadocDocletOptions).apply {
        val javaVersionNumber = compatibilityVersion.toString().replace(Regex(".*\\."), "")
        links("https://docs.oracle.com/${javadocPath}javase/$javaVersionNumber/docs/api")
        addStringOption("Xdoclint:none", "-quiet")
        addStringOption("source", javaVersionNumber)
    }
}

tasks.named("sonar") {
    mustRunAfter(tasks.named("check"))
}

publishing {
    publications {

        create<MavenPublication>("mavenJava") {
            afterEvaluate {
                artifactId = "jenkins-rest-api"
            }
            from(components["java"])
            pom {
                name.set("API library for controlling a Jenkins instance via REST")
                description.set("A Jenkins version 2.458-slim rest API that uses Jackson Json annotations and eclipse microprofile client builder.")
                licenses {
                    license {
                        name.set("Apache License Version 2.0")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            url = uri(
                if (version.toString()
                        .endsWith("SNAPSHOT")
                ) "${extra["ARTIFACT_REPO"]}/maven-snapshots" else "${extra["ARTIFACT_REPO"]}/maven-releases"
            )
            isAllowInsecureProtocol = true
            credentials {
                fun sys(name: String): String {
                    val sysProp: String = System.getenv(name) ?: "must-define-externally"
                    return sysProp
                }
                username = sys("PUBLISHER")
                password = sys("PUBLISHER_PASS")
            }
        }
    }
}
