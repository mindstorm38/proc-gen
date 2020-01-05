
description = rootProject.description + " Client"

dependencies {
    "compile"(project(":pg-common"))
    "compile"(files(project.ext["msengineClient"]))
}

val main = "fr.theorozier.procgen.client.Main"

tasks.named<Jar>("jar") {
    manifest {
        attributes(mapOf("Main-Class" to main))
    }
}

tasks.create<JavaExec>("run") {

    dependsOn("shadowJar")
    main = main
    classpath = files(tasks.named<Jar>("shadowJar").get().archiveFile)

}