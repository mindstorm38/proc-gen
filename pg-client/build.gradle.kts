
description = rootProject.description + " Client"

dependencies {
    "compile"(project(":sutil"))
    "compile"(project(":msengine"))
    "compile"(project(":pg-common"))
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(mapOf("Main-Class" to "fr.theorozier.procgen.client.Main"))
    }
}