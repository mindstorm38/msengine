rootProject.name = "msengine"

include(":sutil")
project(":sutil").projectDir = File("../sutil")

include("common", "client")
