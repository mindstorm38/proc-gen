
description = rootProject.description + " Client"

dependencies {

    "implementation"(project(":pg-common"))
    "implementation"("fr.theorozier:msengine-client:${project.ext["msengineVersion"]}")

}

val mainClass = "fr.theorozier.procgen.client.Main"

tasks.named<Jar>("jar") {
    manifest {
        attributes(mapOf("Main-Class" to mainClass))
    }
}

tasks.create<JavaExec>("run") {

    dependsOn("shadowJar")
    main = mainClass
    classpath = files(tasks.named<Jar>("shadowJar").get().archiveFile)
    // jvmArgs = listOf("-XX:+UnlockCommercialFeatures", "-XX:+FlightRecorder")

}