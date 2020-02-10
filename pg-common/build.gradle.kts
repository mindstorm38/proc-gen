import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

description = rootProject.description + " Common Lib"

tasks.named<ShadowJar>("shadowJar") {
    enabled = false
}

dependencies {
    "implementation"("com.github.luben:zstd-jni:1.4.4-7")
}