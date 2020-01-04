include(":sutil", ":msengine")
include("pg-common", "pg-client", "pg-server")

project(":sutil").projectDir = File("../sutil")
project(":msengine").projectDir = File("../msengine")

rootProject.name = "proc-gen"