dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

val artifactRepo: String? = System.getenv("ARTIFACT_REPO")
val artifactUser: String? = System.getenv("ARTIFACT_USER")
val artifactPass: String? = System.getenv("ARTIFACT_PASS")

artifactRepo?.let { extra.set("ARTIFACT_REPO", it) }
artifactUser?.let { extra.set("ARTIFACT_USER", it) }
artifactPass?.let { extra.set("ARTIFACT_PASS", it) }

val prop = java.util.Properties().apply {
    load(java.io.FileInputStream(File("$rootDir/../gradle.properties")))
}
prop.forEach {
    extra.apply {
        if (!has(it.key.toString())) {
            set(it.key.toString(), it.value)
        }
    }
}

apply(from = "$rootDir/../repos.gradle.kts")
