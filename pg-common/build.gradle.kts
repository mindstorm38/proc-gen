
description = rootProject.description + " Common Lib"

/*
tasks.named<Jar>("jar") {
    enabled = false
}
*/

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    enabled = false
}

dependencies {
    "compile"(project(":sutil"))
    "compile"(project(":msengine"))
}