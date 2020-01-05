
description = rootProject.description + " Common Lib"

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    enabled = false
}

dependencies {
    "compile"(files(project.ext["msengineCommon"]))
}