val javaShortVersion = libs.versions.javaShortVersion.get()

plugins {
    `kotlin-dsl`
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaShortVersion))
    }
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.plugin.ktlint)
    implementation(libs.plugin.detekt)
    implementation(libs.plugin.sonarqube)
    implementation(libs.plugin.kover)
}
