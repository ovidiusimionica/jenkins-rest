dependencyLocking {
  lockAllConfigurations()
}

tasks.register("resolveAndLockAll") {
  val files = configurations.filter { it.isCanBeResolved }.map { it.resolve() }
  doLast {
    val resolvedFiles = files
  }
}





