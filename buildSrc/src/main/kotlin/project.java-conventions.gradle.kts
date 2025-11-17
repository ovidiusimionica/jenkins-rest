import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()
val javaVersion = libs.versions.java.get()
val javaShortVersion = libs.versions.javaShortVersion.get()

fun Project.ancestors(): List<Project> = if (parent != null) {
    parent!!.ancestors() + parent!!
} else {
    emptyList()
}

fun ancestorNamesJoinedBy(separator: String) = ancestors().joinToString(separator) { it.name }

val artifactId = "${ancestorNamesJoinedBy("-")}-$name"
val jarName = "$artifactId-$version"

plugins {
    id("java")
    id("org.sonarqube")
    id("org.jetbrains.kotlinx.kover")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaShortVersion))
    }
}

dependencies {
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src/main/java"))
    }

    test {
        java.setSrcDirs(listOf("src/test/java"))
    }
}


tasks.withType<Jar>() {
    archiveFileName.set("$jarName.jar")
    archiveBaseName.set(artifactId)
}

tasks.withType<Test> {
    systemProperty("user.timezone", "UTC")
    maxHeapSize = "2g"
    testLogging {
        events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = false
        showStackTraces = false
    }
}

tasks.withType<BaseKtLintCheckTask> {
    workerMaxHeapSize.set("512m")
}

kover {
    useJacoco()
}

sonarqube {
    properties {
        property("sonar.language", "java")
        property("sonar.sources", "src/main/java")
        property("sonar.tests", "src/test/java")
        property("sonar.exclusions", "src/generated")
        property("sonar.junit.reportsPath", "build/test-results/test")
        property("sonar.binaries", "build/classes/java")
        property("sonar.java.binaries", "build/classes/java")
        property("sonar.coverage.jacoco.xmlReportPaths", "$rootDir/build/reports/kover/merged/xml/report.xml")
        property("sonar.core.codeCoveragePlugin", "jacoco")
        property("sonar.verbose", "true")
        property("sonar.dynamicAnalysis", "reuseReports")
    }
}
