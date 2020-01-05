
buildscript {

    repositories {
        jcenter()
    }

    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }

}

typealias ShadowJar = com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

allprojects {

    version = "0.1.0"

}

subprojects {

    val subproj = this

    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
    }

    ext {
        set("msengineClient", "../libs/client-1.0.3-all.jar")
        set("msengineCommon", "../libs/common-1.0.3-all.jar")
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.named<JavaCompile>("compileJava") {
        options.encoding = "UTF-8"
    }

}

description = "Proc Gen"

tasks.create<Copy>("distrib") {

    group = "shadow"
    dependsOn("pg-client:shadowJar", "pg-server:shadowJar")

    from(
            project("pg-client").tasks.named<ShadowJar>("shadowJar").get().archiveFile,
            project("pg-server").tasks.named<ShadowJar>("shadowJar").get().archiveFile
    )

    into("build/libs")

}