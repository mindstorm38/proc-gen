
allprojects {

    version = "b1.0.0"
    description = "Procedural Generation Test"

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

}

/*
apply(plugin = "java")

version = "b1.0.0"
description = "Procedural Generation Test"

repositories {
    mavenCentral()
}

dependencies {
    "compile"(project(":sutil"))
    "compile"(project(":msengine"))
}

project.the<SourceSetContainer>()["main"].java {
     exclude("fr/theorozier/procgen/world/**")
}

tasks.register<JavaExec>("runMain") {

    dependsOn("jar")

    main = "fr.theorozier.procgen.client.Main"
    classpath = files(tasks.named<Jar>("jar").get().archiveFile.get())
    classpath += project.the<SourceSetContainer>()["main"].runtimeClasspath

}
*/