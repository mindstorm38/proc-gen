import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

buildscript {

    repositories {
        jcenter()
    }

    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }

}

allprojects {

    version = "0.1.1"

}

subprojects {

    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")

    ext {
        set("msengineVersion", "1.0.7")
        set("sutilVersion", "1.1.0")
    }

    repositories {

        mavenCentral {
            metadataSources {
                mavenPom()
                ignoreGradleMetadataRedirection()
            }
        }

        maven {
            url = uri("https://oss.sonatype.org/content/groups/public/")
            metadataSources {
                mavenPom()
                ignoreGradleMetadataRedirection()
            }
        }

    }

    dependencies {
        "implementation"("fr.theorozier:sutil:${project.ext["sutilVersion"]}")
        "implementation"("fr.theorozier:msengine-common:${project.ext["msengineVersion"]}")
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.named<JavaCompile>("compileJava") {
        options.encoding = "UTF-8"
    }

    tasks.named<ShadowJar>("shadowJar") {
        exclude("*.dll.git", "*.dll.sha1")
        exclude("*.dylib.git", "*.dylib.sha1")
        exclude("*.so.git", "*.so.sha1")
    }

    tasks.register("showConf") {
        configurations.named("runtimeClasspath").get().forEach { println(it) }
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