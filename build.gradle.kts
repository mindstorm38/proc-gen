
buildscript {

    repositories {
        jcenter()
    }

    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }

}

description = "Proc Gen"

allprojects {

    apply(plugin = "com.github.johnrengelman.shadow")

    version = "b1.0.0"

}

subprojects {

    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.named<JavaCompile>("compileJava") {
        options.encoding = "UTF-8"
    }

}


/*
tasks.register<JavaExec>("runMain") {

    dependsOn("jar")

    main = "fr.theorozier.procgen.client.Main"
    classpath = files(tasks.named<Jar>("jar").get().archiveFile.get())
    classpath += project.the<SourceSetContainer>()["main"].runtimeClasspath

}
*/